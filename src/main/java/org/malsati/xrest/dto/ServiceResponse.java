package org.malsati.xrest.dto;

import org.malsati.xrest.dto.errors.AppError;

import java.util.ArrayList;

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
