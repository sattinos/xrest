package com.malsati.simple_web_app.service;

import com.malsati.simple_web_app.dto.book.*;
import com.malsati.simple_web_app.entities.Book;
import com.malsati.simple_web_app.infrastructure.BookRepository;
import com.malsati.simple_web_app.mapper.BookMapper;
import com.malsati.xrest.service.CrudServiceORM;
import org.springframework.stereotype.Service;

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
}
