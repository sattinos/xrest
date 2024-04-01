package com.malsati.simple_web_app.controllers;

import com.malsati.simple_web_app.dto.book.*;
import com.malsati.simple_web_app.entities.Book;
import com.malsati.simple_web_app.service.BookService;
import com.malsati.xrest.controller.CrudController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/app/book")
@RestController
public class BooksController extends CrudController<Book,
        Long,
        CreateOneBookInputDto,
        CreateOneBookOutputDto,
        UpdateOneBookInputDto,
        DeleteOneBookOutputDto,
        GetOneBookOutputDto> {
    public BooksController(BookService bookService) {
        super(bookService);
    }
}