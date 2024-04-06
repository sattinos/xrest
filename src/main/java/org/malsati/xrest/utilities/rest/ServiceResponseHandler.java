package org.malsati.xrest.utilities.rest;

import org.malsati.xrest.dto.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ServiceResponseHandler {

    public static <T> ResponseEntity<ServiceResponse<T>> toResponseEntityCreated(
            ServiceResponse<T> response
    ) {
        return toResponseEntity(response, HttpStatus.OK, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<ServiceResponse<T>> toResponseEntityOk(
            ServiceResponse<T> response
    ) {
        return toResponseEntity(response, HttpStatus.OK, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<ServiceResponse<T>> toResponseEntity(
            ServiceResponse<T> response,
            HttpStatus successCode,
            HttpStatus failCode
    ) {
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, successCode);
        }
        return new ResponseEntity<>(response, failCode);
    }
}