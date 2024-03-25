package com.malsati.xrest.mapper;

import org.springframework.data.domain.Page;

import com.malsati.xrest.dto.pagination.PaginatedResponse;

import java.util.function.Function;

public class PaginationMapper {
    public static <T> PaginatedResponse<T> mapPageToPaginatedResponse(Page<T> page) {
        return new PaginatedResponse<>(
                page.getNumber() + 1, // currentPage is 1-based, Page.getNumber() is 0-based
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getContent()
        );
    }

    public static <T, U> PaginatedResponse<U> mapPageToPaginatedResponse(Page<T> page,
                                                                         Function<? super T, ? extends U> converter) {
        Page<U> resultsMappedToDto = page.map(converter);
        return mapPageToPaginatedResponse(resultsMappedToDto);
    }
}