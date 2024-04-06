package org.malsati.xrest.service;

import java.io.Serializable;
import java.util.List;

import org.malsati.xrest.dto.pagination.PaginatedResponse;
import org.malsati.xrest.dto.ServiceResponse;
import org.springframework.data.domain.Pageable;

public interface CrudService<T,
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