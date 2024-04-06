package org.malsati.simple_web_app.service;

import org.malsati.simple_web_app.dto.author.*;
import org.malsati.simple_web_app.dto.author.*;
import org.malsati.simple_web_app.entities.Author;
import org.malsati.simple_web_app.infrastructure.AuthorRepository;
import org.malsati.simple_web_app.infrastructure.BookRepository;
import org.malsati.simple_web_app.mapper.AuthorMapper;

import org.malsati.xrest.dto.errors.AppError;
import org.malsati.xrest.dto.errors.ErrorCode;
import org.malsati.xrest.service.CrudServiceORM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthorsService extends CrudServiceORM<
        Author,
        Long,
        CreateOneAuthorInputDto,
        CreateOneAuthorOutputDto,
        UpdateOneAuthorInputDto,
        DeleteOneAuthorOutputDto,
        GetOneAuthorOutputDto> {
    public AuthorsService(
            AuthorRepository authorRepository,
            AuthorMapper mapper) {
        super(authorRepository, mapper);
    }

    @Autowired
    BookRepository bookRepository;

    @Override
    protected void onPreCreateOne(CreateOneAuthorInputDto createOneAuthorInputDto, Author entityToCreate) {
        if (createOneAuthorInputDto.getBookIds() != null &&
                !createOneAuthorInputDto.getBookIds().isEmpty()) {
            var books = bookRepository.findAllById(createOneAuthorInputDto.getBookIds());
            for (var book : books) {
                book.getAuthors().add(entityToCreate);
            }
            entityToCreate.setBooks(books);
        }
    }

    @Override
    public ArrayList<AppError> validateCreateOneInput(CreateOneAuthorInputDto createOneAuthorInputDto) {
        ArrayList<AppError> validations = new ArrayList<>();
        AuthorRepository repo = (AuthorRepository) this.jpaRepository;
        if (repo.existsByFullName(createOneAuthorInputDto.getFullName())) {
            validations.add(
                    new AppError(ErrorCode.AlreadyFound, "author full fullName already found.", createOneAuthorInputDto.getFullName())
            );
        }
        if (createOneAuthorInputDto.getBookIds() == null) {
            validations.add(
                    new AppError(ErrorCode.RequiredField, "required field: bookIds")
            );
        }

        if (createOneAuthorInputDto.getBookIds() != null) {
            for (var bookId : createOneAuthorInputDto.getBookIds()) {
                if (!bookRepository.existsById(bookId)) {
                    validations.add(
                            new AppError(ErrorCode.InvalidInput, "book id = %d not found".formatted(bookId), bookId)
                    );
                    break;
                }
            }
        }
        return validations;
    }

    @Override
    public ArrayList<AppError> validateUpdateOneInput(UpdateOneAuthorInputDto updateOneAuthorInputDto, Author author) {
        ArrayList<AppError> validations = new ArrayList<>();
        AuthorRepository repo = (AuthorRepository) this.jpaRepository;
        if (updateOneAuthorInputDto.getFullName() != null &&
                author.getFullName() != null &&
                !author.getFullName().contentEquals(updateOneAuthorInputDto.getFullName()) &&
                repo.existsByFullName(updateOneAuthorInputDto.getFullName())) {
            validations.add(
                    new AppError(ErrorCode.AlreadyFound, "author fullname already found.", updateOneAuthorInputDto.getFullName())
            );
        }
        return validations;
    }

    @Override
    protected void onPreUpdateOne(UpdateOneAuthorInputDto updateOneAuthorInputDto, Author author) {
        if (updateOneAuthorInputDto.getFullName() != null) {
            author.setFullName(updateOneAuthorInputDto.getFullName());
        }
        if (updateOneAuthorInputDto.getBirthDate() != null) {
            author.setBirthDate(updateOneAuthorInputDto.getBirthDate());
        }
        if (updateOneAuthorInputDto.getBookIds() != null) {
            var books = bookRepository.findAllById(updateOneAuthorInputDto.getBookIds());
            if( !books.isEmpty() ) {
                author.setBooks(books);
            }
        }
    }

    @Override
    protected void onPreDeleteOne(Author authorToDelete) {
        if (authorToDelete.getBooks() != null && !authorToDelete.getBooks().isEmpty()) {
            authorToDelete.getBooks().clear();
        }
    }
}
