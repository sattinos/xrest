package com.malsati.xrest.mapper;

import com.malsati.xrest.entities.BaseEntity;

import java.io.Serializable;
import java.util.List;

public interface IMapper<T extends BaseEntity<TKeyType>,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto,
        DeleteOneOutputDto,
        GetOneOutputDto> {
    T createOneInputDtoToEntity(CreateOneInputDto createOneInputDto);
    CreateOneOutputDto entityToCreateOneOutputDto(T entity);
    List<T> createManyInputDtoToEntities(Iterable<CreateOneInputDto> createManyInputDto);
    List<CreateOneOutputDto> entitiesToCreateManyOutputDto(List<T> entities);
    T updateOneInputDtoToEntity(UpdateOneInputDto updateOneInputDto);
    DeleteOneOutputDto entityToDeleteOneOutputDto(T entity);
    List<DeleteOneOutputDto> entitiesToDeleteManyOutputDto(List<T> entity);
    GetOneOutputDto entityToGetOneoutputDto(T entity);
}
