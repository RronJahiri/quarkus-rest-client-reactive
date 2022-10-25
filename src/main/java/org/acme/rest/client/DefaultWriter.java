package org.acme.rest.client;

import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyWriter;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonString;
import javax.json.bind.Jsonb;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DefaultWriter<T> implements ServerMessageBodyWriter<T> {
    // this is called if no other writer returns isWritable true

    @Inject
    public Jsonb jsonb;

    @Override
    public boolean isWriteable(
            Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(
            T t,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws IOException, WebApplicationException {
        httpHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON);
        // gson by default escapes characters
        if (type == String.class) {
            // this way we unescape
            entityStream.write(
                    jsonb
                            .toJson(Json.createValue((String) t), JsonString.class)
                            .getBytes(StandardCharsets.UTF_8));
        } else {
            entityStream.write(jsonb.toJson(t, type).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public boolean isWriteable(
            Class<?> type, Type genericType, ResteasyReactiveResourceInfo target, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeResponse(T o, Type genericType, ServerRequestContext context)
            throws WebApplicationException, IOException {
        if (genericType == String.class) {
            // this way we unescape
            context
                    .getOrCreateOutputStream()
                    .write(jsonb.toJson(Json.createValue((String) o), JsonString.class).getBytes(StandardCharsets.UTF_8));
        } else {
            context.getOrCreateOutputStream()
                    .write(jsonb.toJson(o, genericType).getBytes(StandardCharsets.UTF_8));
        }
    }
}
