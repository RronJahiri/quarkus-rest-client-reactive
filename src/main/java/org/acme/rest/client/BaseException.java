package org.acme.rest.client;

import javax.ws.rs.core.Response;
import java.util.List;

public class BaseException extends RuntimeException {
    public int code;
    public List<Field> fields;

    public BaseException() {
    }

    public BaseException(int code) {
        super("Exception");
        this.code = code;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, List<Field> fields) {
        super(message);
        this.code = code;
        this.fields = fields;
    }

    public BaseException(Response.Status status) {
        super(status.getReasonPhrase());
        this.code = status.getStatusCode();
    }

    public BaseException(Response.StatusType statusInfo) {
        super(statusInfo.getReasonPhrase());
        this.code = statusInfo.getStatusCode();
    }

    public BaseException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseException that = (BaseException) o;
        return code == that.code;
    }

    public static class Field {
        public String name;
        public String error;

        public Field(String name, String error) {
            this.name = name;
            this.error = error;
        }
    }
}
