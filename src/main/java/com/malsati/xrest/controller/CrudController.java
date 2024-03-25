package com.malsati.xrest.controller;

import java.io.Serializable;
import java.util.List;

import com.malsati.xrest.dto.ServiceResponse;
import com.malsati.xrest.dto.pagination.PaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.malsati.xrest.entities.BaseEntity;
import com.malsati.xrest.service.CrudService;

public abstract class CrudController<T extends BaseEntity<TKeyType>,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto,
        DeleteOneOutputDto,
        GetOneOutputDto> {
    private CrudService<T,
            TKeyType,
            CreateOneInputDto,
            CreateOneOutputDto,
            UpdateOneInputDto,
            DeleteOneOutputDto,
            GetOneOutputDto> crudService;

    public CrudController(
            CrudService<T,
                    TKeyType,
                    CreateOneInputDto,
                    CreateOneOutputDto,
                    UpdateOneInputDto,
                    DeleteOneOutputDto,
                    GetOneOutputDto> crudService) {
        this.crudService = crudService;
    }

    @PostMapping(CrudEndpoints.CREATE_ONE)
    protected ResponseEntity<ServiceResponse<CreateOneOutputDto>> createOne(@RequestBody CreateOneInputDto createInputDto) {
        var res = this.crudService.createOne(createInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    @PostMapping(CrudEndpoints.CREATE_MANY)
    protected ResponseEntity<ServiceResponse<CreateOneOutputDto[]>> createMany(@RequestBody Iterable<CreateOneInputDto> createInputDto) {
        var res = this.crudService.createMany(createInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    @PatchMapping(CrudEndpoints.UPDATE_ONE)
    protected ResponseEntity<ServiceResponse<Boolean>> updateOne(@RequestBody UpdateOneInputDto updateOneInputDto) {
        var res = this.crudService.updateOne(updateOneInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(CrudEndpoints.UPDATE_MANY)
    protected ResponseEntity<ServiceResponse<Boolean>> updateMany(@RequestBody Iterable<UpdateOneInputDto> updateManyInputDto) {
        var res = this.crudService.updateMany(updateManyInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(CrudEndpoints.GET_ONE + "/{id}")
    protected ResponseEntity<ServiceResponse<GetOneOutputDto>> getOne(@PathVariable TKeyType id) {
        var res = this.crudService.getOneById(id);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(CrudEndpoints.GET_ONE)
    protected ResponseEntity<ServiceResponse<GetOneOutputDto>> getOne(
            @RequestBody(required = false) String condition
    ) {
        var res = this.crudService.getOne(condition);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(CrudEndpoints.GET_MANY)
    protected ResponseEntity<ServiceResponse<PaginatedResponse<GetOneOutputDto>>> getMany(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestBody(required = false) String condition
    ) {
        var sort = Sort.by(sortBy);
        Pageable pageRequest = PageRequest.of(pageNo - 1, pageSize, sortDir.equalsIgnoreCase("ASC") ? sort.ascending() : sort.descending());
        var res = this.crudService.getMany(condition, pageRequest);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(CrudEndpoints.COUNT)
    protected ResponseEntity<ServiceResponse<Long>> count(
            @RequestBody(required = false) String condition
    ) {
        var res = this.crudService.count(condition);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(CrudEndpoints.DELETE_ONE + "/{id}")
    protected ResponseEntity<ServiceResponse<DeleteOneOutputDto>> deleteOneById(
            @PathVariable TKeyType id
    ) {
        var res = crudService.deleteOneById(id);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(CrudEndpoints.DELETE_MANY)
    protected ResponseEntity<ServiceResponse<List<DeleteOneOutputDto>>> deleteMany(
            @RequestBody(required = false) String condition
    ) {
        var res = crudService.deleteMany(condition);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}