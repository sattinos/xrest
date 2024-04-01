package com.malsati.simple_web_app.service;

import com.malsati.simple_web_app.dto.book.*;
import com.malsati.simple_web_app.entities.Book;
import com.malsati.simple_web_app.infrastructure.AuthorRepository;
import com.malsati.simple_web_app.infrastructure.BookRepository;
import com.malsati.simple_web_app.mapper.BookMapper;
import com.malsati.xrest.dto.errors.AppError;
import com.malsati.xrest.dto.errors.ErrorCode;
import com.malsati.xrest.service.CrudServiceORM;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class BookService extends CrudServiceORM<
        Book,
        Long,
        CreateOneBookInputDto,
        CreateOneBookOutputDto,
        UpdateOneBookInputDto,
        DeleteOneBookOutputDto,
        GetOneBookOutputDto
        > {

    public BookService(BookRepository bookRepository,
                       BookMapper mapper) {
        super(bookRepository, mapper);
    }

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public ArrayList<AppError> validateUpdateOneInput(UpdateOneBookInputDto updateOneBookInputDto, Book entity) {
        ArrayList<AppError> validations = new ArrayList<>();

        if (updateOneBookInputDto.getAuthorsIds() != null) {
            for (var id : updateOneBookInputDto.getAuthorsIds()) {
                if (!authorRepository.existsById(id)) {
                    validations.add(
                            new AppError(ErrorCode.InvalidInput, String.format("author doesn't exist id = %d", id), id)
                    );
                }
            }
        }
        return validations;
    }
    @Override
    protected void onPreCreateOne(CreateOneBookInputDto createOneBookInputDto, Book entityToCreate) {
        if (createOneBookInputDto.getAuthorsIds() != null &&
                !createOneBookInputDto.getAuthorsIds().isEmpty()) {
            var authors = authorRepository.findAllById(createOneBookInputDto.getAuthorsIds());
            for (var author : authors) {
                author.getBooks().add(entityToCreate);
            }
            entityToCreate.setAuthors(authors);
        }
    }

    @Override
    protected void onPreUpdateOne(UpdateOneBookInputDto updateOneBookInputDto, Book entityToUpdate) {
        if (updateOneBookInputDto.getAuthorsIds() != null &&
                !updateOneBookInputDto.getAuthorsIds().isEmpty()) {

            var oldAuthors = entityToUpdate.getAuthors();
            if (oldAuthors != null) {
                for (var oldAuthor : oldAuthors) {
                    oldAuthor.getBooks().removeIf(book -> Objects.equals(book.getId(), entityToUpdate.getId()));
                }
            }
            var authors = authorRepository.findAllById(updateOneBookInputDto.getAuthorsIds());
            for (var author : authors) {
                author.getBooks().add(entityToUpdate);
            }
        }
    }

    @Override
    protected void onPreDeleteOne(Book entityToDelete) {
        if (entityToDelete.getAuthors() != null && !entityToDelete.getAuthors().isEmpty()) {
            var authors = entityToDelete.getAuthors();
            if (authors != null) {
                for (var author : authors) {
                    author.getBooks().removeIf(book -> Objects.equals(book.getId(), entityToDelete.getId()));
                }
            }
            entityToDelete.getAuthors().clear();
        }
    }
}
