package org.malsati.xrest.dto;

import org.malsati.xrest.dto.errors.AppError;

import java.util.ArrayList;

/**
 * This record is a convenient way to return two types: in case of success and in case of failure.
 * @param data custom data in case of success. can be any type.
 * @param isSuccess a flag that indicates success case or failure case
 * @param errors an array of {@link AppError} in case of failure
 * @param <T> the type of custom data in case of success <br>
 *
 * example success case:
 * <pre>
 * {
 *     "data": [
 *         {
 *             "fullName": "John Doe",
 *             "birthDate": "1990-01-01",
 *             "bookIds": [],
 *             "id": 9
 *         },
 *         {
 *             "fullName": "Jane Smith",
 *             "birthDate": "1988-05-15",
 *             "bookIds": [],
 *             "id": 10
 *         }
 *     ],
 *     "isSuccess": true
 * }
 * </pre>
 *
 * example failure case:
 * <pre>
 * {
 *     "isSuccess": false,
 *     "errors": [
 *          {
 *               "errorCode": "5007",
 *               "message": "author full fullName already found.",
 *               "errorData": "John Steward"
 *          },
 *          {
 *               "errorCode": "5012",
 *               "message": "non-latin characters used.",
 *               "errorData": "@@بيسش"
 *          }
 *     ]
 *}
 * </pre>
 *
 */
public record ServiceResponse<T> (T data, boolean isSuccess, AppError[] errors) {
    public ServiceResponse(T data, boolean isSuccess, AppError[] errors) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.errors = errors;
    }

    public ServiceResponse(T data) {
        this(data, true, null);
    }

    public ServiceResponse(AppError... appErrors) {
        this(null, false, appErrors);
    }

    public ServiceResponse(ArrayList<AppError> appErrors) {
        this(null, false, appErrors.toArray(new AppError[0]));
    }
}
