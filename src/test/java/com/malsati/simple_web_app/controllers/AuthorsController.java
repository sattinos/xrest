package com.malsati.simple_web_app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.malsati.simple_web_app.dto.author.*;
import com.malsati.simple_web_app.entities.Author;
import com.malsati.simple_web_app.service.AuthorsService;
import com.malsati.xrest.controller.CrudController;

@RequestMapping("/app/author")
@RestController
public class AuthorsController extends CrudController<Author,
        Long,
        CreateOneAuthorInputDto,
        CreateOneAuthorOutputDto,
        UpdateOneAuthorInputDto,
        DeleteOneAuthorOutputDto,
        GetOneAuthorOutputDto> {
    public AuthorsController(AuthorsService authorsService) {
        super(authorsService);
    }
}