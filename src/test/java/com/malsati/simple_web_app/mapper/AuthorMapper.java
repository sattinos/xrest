package com.malsati.simple_web_app.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.malsati.simple_web_app.dto.author.*;
import com.malsati.simple_web_app.entities.Author;
import com.malsati.simple_web_app.entities.Book;
import com.malsati.xrest.mapper.IMapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AuthorMapper extends IMapper<Author,
        Long,
        CreateOneAuthorInputDto,
        CreateOneAuthorOutputDto,
        UpdateOneAuthorInputDto,
        DeleteOneAuthorOutputDto,
        GetOneAuthorOutputDto> {

    @Override
    @Mapping(source = "bookIds", target = "books")
    Author createOneInputDtoToEntity(CreateOneAuthorInputDto createOneAuthorInputDto);
    @Override
    @Named("createOne")
    @Mapping(source = "books", target = "bookIds")
    CreateOneAuthorOutputDto entityToCreateOneOutputDto(Author entity);
    @Override
    List<Author> createManyInputDtoToEntities(Iterable<CreateOneAuthorInputDto> createManyInputDto);
    @Override
    @IterableMapping(qualifiedByName = "createOne")
    List<CreateOneAuthorOutputDto> entitiesToCreateManyOutputDto(List<Author> entities);
    @Override
    @Mapping(source = "bookIds", target = "books")
    Author updateOneInputDtoToEntity(UpdateOneAuthorInputDto updateOneAuthorInputDto);
    @Override
    @Mapping(source = "books", target = "bookIds")
    DeleteOneAuthorOutputDto entityToDeleteOneOutputDto(Author entity);
    @Override
    List<DeleteOneAuthorOutputDto> entitiesToDeleteManyOutputDto(List<Author> entity);
    @Override
    GetOneAuthorOutputDto entityToGetOneoutputDto(Author entity);

    default List<Book> mapBookIdsToBooks(Collection<Long> bookIds) {
        var books = new ArrayList<Book>(bookIds.size());
        for (Long id: bookIds) {
            books.add(new Book(id));
        }
        return books;
    }

    default List<Long> mapBooksToBookIds(Collection<Book> books) {
        var bookIds = new ArrayList<Long>(books.size());
        for (var book: books) {
            bookIds.add(book.getId());
        }
        return bookIds;
    }
}
