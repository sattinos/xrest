package org.malsati.simple_web_app.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.malsati.simple_web_app.dto.author.*;
import org.mapstruct.*;

import org.malsati.simple_web_app.entities.Author;
import org.malsati.simple_web_app.entities.Book;
import org.malsati.xrest.mapper.IMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
    void updateOneInputDtoToEntity(UpdateOneAuthorInputDto updateOneAuthorInputDto, @MappingTarget Author author);
    @Override
    @Mapping(source = "books", target = "bookIds")
    DeleteOneAuthorOutputDto entityToDeleteOneOutputDto(Author entity);
    @Override
    List<DeleteOneAuthorOutputDto> entitiesToDeleteManyOutputDto(List<Author> entity);
    @Override
    GetOneAuthorOutputDto entityToGetOneOutputDto(Author entity);

    default List<Book> mapBookIdsToBooks(Collection<Long> bookIds) {
        if( bookIds == null) {
            return new ArrayList<>();
        }
        var books = new ArrayList<Book>(bookIds.size());
        for (Long id: bookIds) {
            books.add(new Book(id));
        }
        return books;
    }

    default List<Long> mapBooksToBookIds(Collection<Book> books) {
        if( books == null) {
            return new ArrayList<>();
        }
        var bookIds = new ArrayList<Long>(books.size());
        for (var book: books) {
            bookIds.add(book.getId());
        }
        return bookIds;
    }
}