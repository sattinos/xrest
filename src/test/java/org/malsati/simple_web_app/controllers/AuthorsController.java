package org.malsati.simple_web_app.controllers;

import org.malsati.simple_web_app.dto.author.*;
import org.malsati.simple_web_app.entities.Author;
import org.malsati.simple_web_app.service.AuthorsService;
import org.malsati.xrest.controller.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A CRUD controller for the Author entity.
 * This is en example of how to create CRUD controller using XRest
 */
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