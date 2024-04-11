package org.malsati.xrest.dto.errors;

/**
 * This is a detailed record of any APP error that might happen.
 * @param errorCode a string value that is taken from the file {@link ErrorCode}
 * @param message the error message that tells what the error is.
 * @param errorData additional data about the error
 * @param exceptionDetails useful in development mode only <br>
 *
 * example1:
 *
 * <pre>
 * {
 *     "errorCode": "5000",
 *     "message": "required field: id."
 * }
 * </pre>
 *
 * example2:
 * <pre>
 * {
 *     "errorCode": "5007",
 *     "message": "The username: 'Octavio' is already used."
 * }
 * </pre>
 */
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