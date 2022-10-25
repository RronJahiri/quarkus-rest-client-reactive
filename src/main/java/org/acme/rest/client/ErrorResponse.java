package org.acme.rest.client;

import java.util.List;

public class ErrorResponse {
    public Error error;

    public ErrorResponse(BaseException baseException) {
        this.error = new Error(baseException);
    }

    public static class Error {
        public int code;
        public String message;
        public List<BaseException.Field> fields;

        public Error(BaseException baseException) {
            this.code = baseException.code;
            this.message = baseException.getMessage();
            this.fields = baseException.fields;
        }
    }
}
