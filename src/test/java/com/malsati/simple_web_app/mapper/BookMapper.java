package com.malsati.simple_web_app.mapper;

import com.malsati.simple_web_app.dto.book.*;
import com.malsati.simple_web_app.entities.Book;
import com.malsati.xrest.mapper.IMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookMapper extends IMapper<
        Book,
        Long,
        CreateOneBookInputDto,
        CreateOneBookOutputDto,
        UpdateOneBookInputDto,
        DeleteOneBookOutputDto,
        GetOneBookOutputDto> {
    @Override
    Book createOneInputDtoToEntity(CreateOneBookInputDto createOneBookInputDto);

    @Override
    @Named("toCreateOne")
    CreateOneBookOutputDto entityToCreateOneOutputDto(Book entity);

    @Override
    List<Book> createManyInputDtoToEntities(Iterable<CreateOneBookInputDto> createManyInputDto);

    @Override
    @IterableMapping(qualifiedByName = "toCreateOne")
    List<CreateOneBookOutputDto> entitiesToCreateManyOutputDto(List<Book> entities);

    @Override
    void updateOneInputDtoToEntity(UpdateOneBookInputDto updateOneBookInputDto, @MappingTarget Book book);

    @Override
    DeleteOneBookOutputDto entityToDeleteOneOutputDto(Book entity);

    @Override
    List<DeleteOneBookOutputDto> entitiesToDeleteManyOutputDto(List<Book> entity);

    @Override
    GetOneBookOutputDto entityToGetOneoutputDto(Book entity);
}
