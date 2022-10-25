package org.acme.rest.client;

import io.smallrye.mutiny.CompositeException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.inject.Inject;
import javax.json.bind.JsonbException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.core.MediaType;

import static org.acme.rest.client.Utils.notBlank;
import static org.acme.rest.client.Utils.notNull;

public class DefaultExceptionMappers {
    @Inject
    Logger logs;

    // region Exception handler
    @ServerExceptionMapper(Exception.class)
    public RestResponse<Object> onException(Exception ex) {
        logs.error(notBlank(ex.getMessage()) ? ex.getMessage() : "Exception: ", ex);
        return createUniResponse(500, "Something went wrong");
    }
    // endregion

    // region BaseException handler
    @ServerExceptionMapper(BaseException.class)
    public RestResponse<Object> onBaseException(BaseException ex) {
        return createUniResponse(ex);
    }
    // endregion

    // region ClientErrorException handler
    @ServerExceptionMapper(ClientErrorException.class)
    public RestResponse<Object> onClientErrorException(ClientErrorException ex) {
        return createUniResponse(ex.getResponse().getStatus(), ex.getMessage());
    }
    // endregion

    // region InternalServerErrorException handler
    @ServerExceptionMapper(InternalServerErrorException.class)
    public RestResponse<Object> onInternalServerErrorException(InternalServerErrorException ex) {
        if (!ignore(ex)) {
            if (printTrance(ex)) {
                logs.error(notBlank(ex.getMessage()) ? ex.getMessage() : "Exception: ", ex);
            } else {
                logMessage(ex);
            }
        }

        return createUniResponse(500, "Something went wrong");
    }
    // endregion

    // region ServiceUnavailableException handler
    @ServerExceptionMapper(ServiceUnavailableException.class)
    public RestResponse<Object> onServiceUnavailableException(ServiceUnavailableException ex) {
        return createUniResponse(503, ex.getMessage());
    }
    // endregion

    // region CompositeException handler
    @ServerExceptionMapper(CompositeException.class)
    public RestResponse<Object> onCompositeException(CompositeException ex) {
        Throwable throwable = ex.getCauses().get(ex.getCauses().size() - 1);

        // print all exceptions
        ex.getCauses().forEach(throwable1 -> logs.error(throwable1.getMessage(), throwable1));

        return createUniResponse(400, throwable.getMessage());
    }
    // endregion

    // region StatusRuntimeException handler

    // endregion

    // region JsonbException handler
    @ServerExceptionMapper(JsonbException.class)
    public RestResponse<Object> onJsonbException(JsonbException ex) {
        if (notNull(ex.getCause()) && notNull(ex.getCause().getCause())) {
            return createUniResponse(new BaseException(ex.getCause().getCause()));
        }
        logs.error(notBlank(ex.getMessage()) ? ex.getMessage() : "JsonbException: ", ex);
        return createUniResponse(500, "There was a problem deserializing from json body");
    }
    //  // endregion

    // region logging utils
    private void logMessage(Throwable exception) {
        if (exception.getMessage() != null) {
            logs.error(exception.getMessage());
        } else {
            exception.printStackTrace();
        }
    }

    // exceptions that we don't need to have traces
    private boolean printTrance(Throwable exception) {
        return !(exception instanceof javax.ws.rs.NotAllowedException
                || exception instanceof javax.ws.rs.NotFoundException);
    }

    private boolean ignore(Throwable exception) {
        return exception instanceof java.io.IOException
                && exception.getMessage() != null
                && exception
                .getMessage()
                .equals("java.io.IOException: io.vertx.core.VertxException: Connection was closed");
    }
    // endregion

    // region response builders
    private ErrorResponse buildErrorResponse(int status, String message) {
        return new ErrorResponse(new BaseException(status, message));
    }

    private RestResponse<Object> createUniResponse(int status, String message) {
        return createUniResponse(new BaseException(status, message));
    }


    private RestResponse<Object> createUniResponse(BaseException exception) {
        return RestResponse.ResponseBuilder.create(exception.code)
                .entity(new ErrorResponse(exception))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

}
