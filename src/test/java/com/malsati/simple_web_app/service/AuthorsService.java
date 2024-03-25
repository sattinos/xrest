package com.malsati.simple_web_app.service;

import com.malsati.simple_web_app.dto.author.*;
import com.malsati.simple_web_app.entities.Author;
import com.malsati.simple_web_app.entities.Book;
import com.malsati.simple_web_app.infrastructure.AuthorRepository;
import com.malsati.simple_web_app.infrastructure.BookRepository;
import com.malsati.simple_web_app.mapper.AuthorMapper;

import com.malsati.xrest.dto.errors.AppError;
import com.malsati.xrest.dto.errors.ErrorCode;
import com.malsati.xrest.service.CrudServiceORM;
import com.malsati.xrest.utilities.tuples.Pair;

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
        if(  createOneAuthorInputDto.getBookIds() != null &&
            !createOneAuthorInputDto.getBookIds().isEmpty()) {
            var books =  bookRepository.findAllById(createOneAuthorInputDto.getBookIds());
            for (var book: books) {
                book.setAuthor(entityToCreate);
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
                    new AppError(ErrorCode.AlreadyFound, "author full name already found.", createOneAuthorInputDto.getFullName())
            );
        }
        if (createOneAuthorInputDto.getBookIds() == null) {
            validations.add(
                    new AppError(ErrorCode.RequiredField, "required field: booksIds")
            );
        }

        if( createOneAuthorInputDto.getBookIds() != null ) {
            for (var bookId: createOneAuthorInputDto.getBookIds()) {
                if( !bookRepository.existsById(bookId) ) {
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
    public Pair<ArrayList<AppError>, Author> validateUpdateOneInput(UpdateOneAuthorInputDto updateOneAuthorInputDto) {
        ArrayList<AppError> validations = new ArrayList<>();
        AuthorRepository repo = (AuthorRepository) this.jpaRepository;

        if (updateOneAuthorInputDto.getId() == null) {
            validations.add(
                    new AppError(ErrorCode.RequiredField, "required field: id.")
            );
            return new Pair<>(validations, null);
        }

        Author author = jpaRepository.findById(updateOneAuthorInputDto.getId()).get();
        if (updateOneAuthorInputDto.getFullName() != null &&
                !author.getFullName().contentEquals(updateOneAuthorInputDto.getFullName()) &&
                repo.existsByFullName(updateOneAuthorInputDto.getFullName())) {
            validations.add(
                    new AppError(ErrorCode.AlreadyFound, "author fullname already found.", updateOneAuthorInputDto.getFullName())
            );
        }
        return new Pair<>(validations, author);
    }

    @Override
    protected void onPreUpdateOne(UpdateOneAuthorInputDto updateOneAuthorInputDto, Author author) {
        if( updateOneAuthorInputDto.getFullName() != null) {
            author.setFullName(updateOneAuthorInputDto.getFullName());
        }
        if(updateOneAuthorInputDto.getBirthDate() != null) {
            author.setBirthDate(updateOneAuthorInputDto.getBirthDate());
        }
        if( updateOneAuthorInputDto.getBookIds() != null) {
            var books = bookRepository.findAllById(updateOneAuthorInputDto.getBookIds());
            setBooksAuthor(books, author);
            setBooksAuthor(author.getBooks(), null);
            author.setBooks(books);
        }
    }

    private void setBooksAuthor(Iterable<Book> books, Author author) {
        for(var book: books) {
            book.setAuthor(author);
        }
    }
}
