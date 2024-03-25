package com.malsati.xrest.dto.errors;

public record AppError(String errorCode,
                       String message,
                       Object errorData,
                       String exceptionDetails) {

    public AppError(String errorCode, String message) {
        this(errorCode, message, null, null);
    }

    public AppError(String errorCode, String message, Object errorData) {
        this(errorCode, message, errorData, null);
    }

    @Override
    public String toString() {
        if( errorData == null ) {
            return """
                {
                    "errorCode": "%s",
                    "message": "%s"
                }
                """.formatted(errorCode, message);
        }

        return """
                {
                    "errorCode": "%s",
                    "message": "%s",
                    "errorData": "%s"
                }
                """.formatted(errorCode, message, errorData);
    }
}