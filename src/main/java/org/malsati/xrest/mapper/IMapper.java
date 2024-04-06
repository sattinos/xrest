package org.malsati.xrest.mapper;

import java.io.Serializable;
import java.util.List;

public interface IMapper<T,
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
    void updateOneInputDtoToEntity(UpdateOneInputDto updateOneInputDto, T entity);
    DeleteOneOutputDto entityToDeleteOneOutputDto(T entity);
    List<DeleteOneOutputDto> entitiesToDeleteManyOutputDto(List<T> entity);
    GetOneOutputDto entityToGetOneOutputDto(T entity);
}
