package org.malsati.xrest.service;

import java.io.Serializable;
import java.util.List;

import org.malsati.xrest.controller.CrudController;
import org.malsati.xrest.dto.pagination.PaginatedResponse;
import org.malsati.xrest.dto.ServiceResponse;
import org.springframework.data.domain.Pageable;


/**
 * This is considered the contract for any CRUD service.
 * @param <T> the entity to have CRUD functionality for
 * @param <TKeyType> the type of the entity key.
 * @param <CreateOneInputDto> The input dto for the CreateOne API
 * @param <CreateOneOutputDto> The output dto for the CreateOne API
 * @param <UpdateOneInputDto> The input dto for the UpdateOne API
 * @param <DeleteOneOutputDto> The output dto for the DeleteOne API
 * @param <GetOneOutputDto> The input dto for the GetOne API
 */
public interface CrudService<T,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto,
        DeleteOneOutputDto,
        GetOneOutputDto> {
    /**
     * Creates an entity based on the input dto you have defined
     * @param createOneInputDto: The input dto of the API
     * @return a service response for the created entity fields represented in CreateOneOutputDto <br>
     * in case of failure it will return an array of errors<br>
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
     *   example failure response:
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
    ServiceResponse<CreateOneOutputDto> createOne(CreateOneInputDto createOneInputDto);

    /**
     *  Creates many entities at once based on the createManyInputDto passed
     *
     * @param createManyInputDto an array of CreateOneInputDto
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
     * @see CrudService#createOne
     */
    ServiceResponse<CreateOneOutputDto[]> createMany(Iterable<CreateOneInputDto> createManyInputDto);

    /**
     * It updates a list of fields in the entity. Please, put null for the fields you don't want to update.
     * @param updateOneInputDto : the input Dto
     * @return a service response with data as true if the update succeeds, otherwise false. <br>
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
    ServiceResponse<Boolean> updateOne(UpdateOneInputDto updateOneInputDto);

    /**
     * It allows to update many entities at once.
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
    ServiceResponse<Boolean> updateMany(Iterable<UpdateOneInputDto> updateManyInputDto);

    /**
     *
     * This API allows for getting an entity by a JSON where condition
     * @param condition The where condition in JSON notation (Optional)
     * @return a service response with the details of an entity that is defined in the output dto <br>
     *
     * example condition:
     * <pre>{@code
     * {
     *     "op": "=",
     *     "lhs": "id",
     *     "rhs": 1
     * }
     * }</pre>
     *
     * example return value:
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
     * @see org.malsati.xrest.infrastructure.jpql.SpecificationBuilder how form a JSON condition
     */
    ServiceResponse<GetOneOutputDto> getOne(String condition);

    /**
     *
     * It allows for getting the entity by its id
     * @param id the id of the entity you request for
     * @return a service response with GetOneOutputDto that you have defined initially. <br>
     *
     * example: <br>
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
    ServiceResponse<GetOneOutputDto> getOneById(TKeyType id);


    /**
     * It allows for retriving a list of entities based on a JSON where condition.
     * This API supports pagination.
     * @param pageable: pagination info
     * @param condition JSON where condition
     * @return service response with Paginated response of the output dto you have defined initially.<br>
     *
     * sample condition:
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
     * sample return value:
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
     * @see org.malsati.xrest.infrastructure.jpql.SpecificationBuilder how form a JSON condition
     */
    ServiceResponse<PaginatedResponse<GetOneOutputDto>> getMany(String condition, Pageable pageable);

    /**
     *
     * It allows for requesting for the count of entities that satisfies a JSON where condition.
     * @param condition JSON where condition (optional). If not specified, all entities count is returned.
     * @return a service response with count of entities.<br>
     *
     * sample condition:
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
     * sample return value:
     *
     * <pre>
     * {
     *     "data": 13,
     *     "isSuccess": true,
     *     "errors": null
     * }
     * </pre>
     *
     * note: if the entity is soft delete, only entities that are not soft deleted are counted.
     * @see org.malsati.xrest.infrastructure.jpql.SpecificationBuilder how form a JSON condition
     */
    ServiceResponse<Long> count(String condition);

    /**
     *
     * It deletes an entity by its Id (soft or hard)
     * @param id The id of the entity to delete
     * @return The deleted entity dto if successfully deleted.
     *
     * sample return value:
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
    ServiceResponse<DeleteOneOutputDto> deleteOneById(TKeyType id);

    /**
     *
     * This API deletes a list of entities that specify a condition in JSON notation
     * @param condition JSON where condition (Mandatory)
     * @return the list of entities dtos that were deleted
     *
     * sample condition: (delete all the authors of the book 'Artificial Intelligence')
     *
     * <pre>
     * {
     *     "op": "=",
     *     "lhs": "books.title",
     *     "rhs": "Artificial Intelligence"
     * }
     * </pre>
     *
     * sample return value:
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
     *     "isSuccess": true,
     *     "errors": null
     * }
     * </pre>
     *
     * note: if the entity is soft delete, only entities that are not soft deleted are deleted.
     * @see org.malsati.xrest.infrastructure.jpql.SpecificationBuilder how form a JSON condition
     */
    ServiceResponse<List<DeleteOneOutputDto>> deleteMany(String condition);
}