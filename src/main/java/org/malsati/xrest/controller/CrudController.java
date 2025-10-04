package org.malsati.xrest.controller;

import java.io.Serializable;
import java.util.List;

import org.malsati.xrest.dto.ServiceResponse;
import org.malsati.xrest.dto.pagination.PaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.malsati.xrest.service.CrudService;

/**
 * This class will give you CRUD functionality to your controller: ( GetOne, GetMany, Count, UpdateMany, GetMany, DeleteMany )
 * As well as the possibility of passing your own condition in
 * JSON notation for the APIs.
 * @see org.malsati.xrest.controller.CrudEndpoints
 * @param <T> the entity to have CRUD functionality for
 * @param <TKeyType> the type of the entity key.
 * @param <CreateOneInputDto> The input dto for the CreateOne API
 * @param <CreateOneOutputDto> The output dto for the CreateOne API
 * @param <UpdateOneInputDto> The input dto for the UpdateOne API
 * @param <DeleteOneOutputDto> The output dto for the DeleteOne API
 * @param <GetOneOutputDto> The input dto for the GetOne API
 */
public abstract class CrudController<T,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto,
        DeleteOneOutputDto,
        GetOneOutputDto> {
    protected CrudService<T,
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

    /**
     * @param createInputDto: The input dto of the API
     * @return in case of success it will return the CreateOneOutputDto with 201 status code. <br>
     * in case of failure it will return an array of errors with 409 status code.<br>
     *    example success response:<br>
     *
     *<pre>
     *    {
     *     "data": {
     *          "fullName": "John Steward",
     *          "birthDate": "1970-12-10",
     *          "bookIds": [
     *               1
     *          ],
     *          "id": 6
     *     },
     *     "isSuccess": true
     *    }
     *</pre>
     *
     * example failure response:
     *<pre>
     * {
     *     "isSuccess": false,
     *     "errors": [
     *          {
     *               "errorCode": "5007",
     *               "message": "author full fullName already found.",
     *               "errorData": "John Steward"
     *          },
     *          {
     *               "errorCode": "5012",
     *               "message": "non-latin characters used.",
     *               "errorData": "@@بيسش"
     *          }
     *     ]
     *}
     *</pre>
     *
     * @see org.malsati.xrest.dto.errors.AppError
     *
     */
    @PostMapping(CrudEndpoints.CREATE_ONE)
    protected ResponseEntity<ServiceResponse<CreateOneOutputDto>> createOne(@RequestBody CreateOneInputDto createInputDto) {
        var res = this.crudService.createOne(createInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    /**
     *  Creates many entities at once based on the CreateManyDto passed
     *
     * @param createInputDto an array of CreateOneInputDto
     * @return an array of CreateOneOutputDto
     * for an example: <br>
     * request:
     * <pre>{@code
     * [
     *     {
     *         "fullName": "Adriana Pineda",
     *         "birthDate": "2000-12-10",
     *         "bookIds": [
     *             3
     *         ]
     *     },
     *     {
     *         "fullName": "Tyree Hansen",
     *         "birthDate": "1975-05-04",
     *         "bookIds": [
     *             4
     *         ]
     *     }
     * ]
     * }
     * </pre>
     *
     * response:
     *
     * <pre>{@code
     * {
     *      "data": [
     *           {
     *                "fullName": "Adriana Pineda",
     *                "birthDate": "2000-12-10",
     *                "bookIds": [
     *                     3
     *                ],
     *                "id": 7
     *           },
     *           {
     *                "fullName": "Tyree Hansen",
     *                "birthDate": "1975-05-04",
     *                "bookIds": [
     *                     4
     *                ],
     *                "id": 8
     *           }
     *      ],
     *      "isSuccess": true
     * }
     * }</pre>
     * @see CrudController#createOne
     */
    @PostMapping(CrudEndpoints.CREATE_MANY)
    protected ResponseEntity<ServiceResponse<List<CreateOneOutputDto>>> createMany(@RequestBody List<CreateOneInputDto> createInputDto) {
        var res = this.crudService.createMany(createInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    /***
     * This API updates a list of fields in the entity. Please, put null for the fields you don't want to update.
     * @param updateOneInputDto : the input Dto of the API.
     * @return a service response with data as true if the update succeeds, otherwise false.
     *
     * sample request:
     *
     * <pre>{@code
     * {
     *      "fullName": "Giancarlo Harmon",
     *      "birthDate": "1960-07-04",
     *      "bookIds": [
     *           13,
     *           3,
     *           6
     *      ],
     *      "id": 5
     * }
     * }</pre>
     *
     * sample response (success case):
     *
     * <pre>{@code
     * {
     *     "data": true,
     *     "isSuccess": true
     * }
     * }</pre>
     *
     * sample response (failure case):
     * <pre>{@code
     * {
     *     "isSuccess": false,
     *     "errors": [
     *         {
     *             "errorCode": "5000",
     *             "message": "required field: id."
     *         }
     *     ]
     * }
     * }</pre>
     */
    @PatchMapping(CrudEndpoints.UPDATE_ONE)
    protected ResponseEntity<ServiceResponse<Boolean>> updateOne(@RequestBody UpdateOneInputDto updateOneInputDto) {
        var res = this.crudService.updateOne(updateOneInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * This API allows to update many entities at once.
     * @param updateManyInputDto: An array of update one input dto
     * @return a service response with data as true if the update succeeds, otherwise false.
     *
     * example request:
     * <pre>{@code
     * [
     *     {
     *         "title": "The Eathquake",
     *         "publishDate": null,
     *         "edition": 4,
     *         "volume": 5,
     *         "press": null,
     *         "noPages": null,
     *         "authorsIds": null,
     *         "id": 11
     *     },
     *     {
     *         "title": "The Edge Of Darkness",
     *         "publishDate": null,
     *         "edition": 2,
     *         "volume": 2,
     *         "press": null,
     *         "noPages": null,
     *         "authorsIds": null,
     *         "id": 12
     *     }
     * ]
     * }</pre>
     *
     * example response:
     * <pre>{@code
     * {
     *     "data": true,
     *     "isSuccess": true
     * }
     * }</pre>
     *
     */
    @PatchMapping(CrudEndpoints.UPDATE_MANY)
    protected ResponseEntity<ServiceResponse<Boolean>> updateMany(@RequestBody List<UpdateOneInputDto> updateManyInputDto) {
        var res = this.crudService.updateMany(updateManyInputDto);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * This API allows for getting the entity by its id
     * @param id the id of the entity you request for
     * @return a service response with GetOneOutputDto that you have defined initially. <br>
     *
     * sample request: /app/author/getOne/1 <br>
     * sample response: <br>
     * <pre>{@code
     * {
     *     "data": {
     *         "id": 1,
     *         "fullName": "J.K. Rowling",
     *         "birthDate": "1965-07-31",
     *         "books": [
     *             {
     *                 "id": 1,
     *                 "title": "Harry Potter and the Philosopher's Stone",
     *                 "publishDate": "1997-06-26",
     *                 "edition": 1,
     *                 "volume": 1,
     *                 "press": "Bloomsbury Publishing",
     *                 "noPages": 223,
     *                 "authors": [
     *                     {
     *                         "id": 1,
     *                         "fullName": "J.K. Rowling"
     *                     },
     *                     {
     *                         "id": 6,
     *                         "fullName": "John Steward"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "id": 2,
     *                 "title": "Harry Potter and the Chamber of Secrets",
     *                 "publishDate": "1998-07-02",
     *                 "edition": 1,
     *                 "volume": 1,
     *                 "press": "Bloomsbury Publishing",
     *                 "noPages": 251,
     *                 "authors": [
     *                     {
     *                         "id": 1,
     *                         "fullName": "J.K. Rowling"
     *                     }
     *                 ]
     *             }
     *         ]
     *     },
     *     "isSuccess": true,
     *     "errors": null
     * }
     * }</pre>
     *
     *
     */
    @GetMapping(CrudEndpoints.GET_ONE + "/{id}")
    protected ResponseEntity<ServiceResponse<GetOneOutputDto>> getOneById(@PathVariable TKeyType id) {
        var res = this.crudService.getOneById(id);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * This API allows for getting an entity by a JSON where condition
     * @param condition The where condition in JSON notation (Optional)
     * @return a service response with the details of an entity that is defined in the output dto
     *
     * example request:
     * <pre>{@code
     * {
     *     "op": "=",
     *     "lhs": "id",
     *     "rhs": 1
     * }
     * }</pre>
     *
     * example response:
     * <pre>{@code
     * {
     *     "data": {
     *         "id": 1,
     *         "title": "Harry Potter and the Philosopher's Stone",
     *         "publishDate": "1997-06-26",
     *         "edition": 1,
     *         "volume": 1,
     *         "press": "Bloomsbury Publishing",
     *         "noPages": 223,
     *         "authors": [
     *             {
     *                 "id": 6,
     *                 "fullName": "John Steward"
     *             }
     *         ]
     *     },
     *     "isSuccess": true,
     *     "errors": null
     * }
     * }</pre>
     *
     * note: if the entity is soft delete, only an entity that is not soft deleted is returned.
     */
    @GetMapping(CrudEndpoints.GET_ONE)
    protected ResponseEntity<ServiceResponse<GetOneOutputDto>> getOneWhere(
            @RequestBody(required = false) String condition
    ) {
        var res = this.crudService.getOne(condition);
        if (res.isSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * This API allows for retriving a list of entities based on a JSON where condition.
     * This API supports pagination.
     * @param pageNo: starts by 1 (Optional)
     * @param pageSize defaults to 20 if not specified
     * @param sortBy defaults to id if not specified.
     * @param sortDir ASC or DESC. Defaults to ASC if not specified.
     * @param condition JSON where condition
     * @return service response with Paginated response of the output dto you have defined initially.<br>
     *
     * example request:
     * <pre>{@code
     *   "op": "&&",
     *   "lhs": {
     *     "op": "<",
     *     "lhs": "publishDate",
     *     "rhs": "2000-12-25",
     *     "type": "Date"
     *   },
     *   "rhs": {
     *     "op": "<",
     *     "lhs": "noPages",
     *     "rhs": 300
     *   }
     * }
     * </pre>
     *
     * example response:
     * <pre>
     * {
     *   "data": {
     *     "currentPage": 1,
     *     "pageSize": 20,
     *     "totalPages": 1,
     *     "totalItems": 4,
     *     "data": [
     *       {
     *         "id": 1,
     *         "title": "Harry Potter and the Philosopher's Stone",
     *         "publishDate": "1997-06-26",
     *         "edition": 1,
     *         "volume": 1,
     *         "press": "Bloomsbury Publishing",
     *         "noPages": 223,
     *         "authors": [
     *           {
     *             "id": 6,
     *             "fullName": "John Steward"
     *           }
     *         ]
     *       },
     *       {
     *         "id": 2,
     *         "title": "Harry Potter and the Chamber of Secrets",
     *         "publishDate": "1998-07-02",
     *         "edition": 1,
     *         "volume": 1,
     *         "press": "Bloomsbury Publishing",
     *         "noPages": 251,
     *         "authors": []
     *       },
     *       {
     *         "id": 5,
     *         "title": "Norwegian Wood",
     *         "publishDate": "1987-09-04",
     *         "edition": 1,
     *         "volume": 1,
     *         "press": "Kodansha",
     *         "noPages": 296,
     *         "authors": []
     *       },
     *       {
     *         "id": 13,
     *         "title": "Game of Thrones 2",
     *         "publishDate": "2000-12-15",
     *         "edition": 2,
     *         "volume": 1,
     *         "press": "HBC",
     *         "noPages": 200,
     *         "authors": [
     *           {
     *             "id": 1,
     *             "fullName": "J.K. Rowling"
     *           },
     *           {
     *             "id": 2,
     *             "fullName": "Stephen King"
     *           }
     *         ]
     *       }
     *     ]
     *   },
     *   "isSuccess": true,
     *   "errors": null
     * }
     * </pre>
     *
     * note: if the entity is soft delete, only entities that are not soft deleted are returned.
     */
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

    /**
     * This API allows for requesting for the count of entities that satisfies a JSON where condition.
     * @param condition JSON where condition (optional). If not specified, all entities count is returned.
     * @return a service response with count of entities.
     *
     * example request:
     *
     * <pre>
     * {
     *     "op": "between",
     *     "lhs": "publishDate",
     *     "range1": "1900-12-12",
     *     "range2": "2010-12-01",
     *     "type": "Date"
     * }
     * </pre>
     *
     * example response:
     *
     * Example JSON response:
     * <pre>
     * {
     *     "data": 13,
     *     "isSuccess": true,
     *     "errors": null
     * }
     * </pre>
     *
     * note: if the entity is soft delete, only entities that are not soft deleted are counted.
     */
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

    /**
     * This API deletes an entity by its Id (soft or hard)
     * @param id The id of the entity to delete
     * @return The deleted entity dto if successfully deleted.
     *
     * example request: /app/book/deleteOne/11
     *
     * example response:
     * <pre>
     * {
     *     "data": {
     *         "title": "Rise Of The Dragons",
     *         "publishDate": "2010-09-14",
     *         "edition": 3,
     *         "volume": 2,
     *         "press": "APress",
     *         "noPages": 670,
     *         "authorsIds": null,
     *         "id": 11
     *     },
     *     "isSuccess": true,
     *     "errors": null
     * }
     * </pre>
     *
     */
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

    /**
     * This API deletes a list of entities that specify a condition in JSON notation
     * @param condition JSON where condition (Mandatory)
     * @return the list of entities dtos that were deleted
     *
     * example request: (delete all the authors of the book 'Artificial Intelligence')
     *
     * <pre>
     * {
     *     "op": "=",
     *     "lhs": "books.title",
     *     "rhs": "Artificial Intelligence"
     * }
     * </pre>
     *
     * example response:
     *
     * <pre>
     * {
     *     "data": [
     *         {
     *             "fullName": "John Doe",
     *             "birthDate": "1990-01-01",
     *             "bookIds": [],
     *             "id": 9
     *         },
     *         {
     *             "fullName": "Jane Smith",
     *             "birthDate": "1988-05-15",
     *             "bookIds": [],
     *             "id": 10
     *         }
     *     ],
     *     "isSuccess": true
     * }
     * </pre>
     *
     * note: if the entity is soft delete, only entities that are not soft deleted are deleted.
     *
     */
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