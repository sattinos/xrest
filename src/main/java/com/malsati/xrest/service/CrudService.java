package com.malsati.xrest.service;

import java.io.Serializable;
import java.util.List;

import com.malsati.xrest.dto.pagination.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import com.malsati.xrest.dto.ServiceResponse;
import com.malsati.xrest.entities.BaseEntity;

public interface CrudService<T extends BaseEntity<TKeyType>,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto,
        DeleteOneOutputDto,
        GetOneOutputDto> {
    ServiceResponse<CreateOneOutputDto> createOne(CreateOneInputDto createOneInputDto);
    ServiceResponse<CreateOneOutputDto[]> createMany(Iterable<CreateOneInputDto> createManyInputDto);
    ServiceResponse<Boolean> updateOne(UpdateOneInputDto updateOneInputDto);
    ServiceResponse<Boolean> updateMany(Iterable<UpdateOneInputDto> updateOneInputDto);
    ServiceResponse<GetOneOutputDto> getOne(String condition);
    ServiceResponse<PaginatedResponse<GetOneOutputDto>> getMany(String condition, Pageable pageable);
    ServiceResponse<GetOneOutputDto> getOneById(TKeyType id);
    ServiceResponse<Long> count(String condition);
    ServiceResponse<DeleteOneOutputDto> deleteOneById(TKeyType id);
    ServiceResponse<List<DeleteOneOutputDto>> deleteMany(String condition);
}