package com.malsati.controllers_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.malsati.simple_web_app.dto.author.*;
import com.malsati.simple_web_app.infrastructure.BookRepository;
import com.malsati.simple_web_app.utils.json.JsonPrinter;
import com.malsati.utilities.json.JsonRestHitter;
import com.malsati.xrest.controller.CrudEndpoints;
import com.malsati.xrest.dto.ServiceResponse;
import com.malsati.xrest.dto.errors.AppError;
import com.malsati.xrest.dto.errors.ErrorCode;
import com.malsati.xrest.dto.pagination.PaginatedResponse;
import com.malsati.xrest.utilities.text.StringExtensions;

import com.malsati.xrest.utilities.tuples.Pair;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorControllerTest {
    private static final String authorControllerBaseUrl = "/app/author";

    @Autowired
    private BookRepository bookRepository;

    private MockMvc restHitter;
    private JsonRestHitter jsonRestHitter;

    private ObjectMapper objectMapper;

    public AuthorControllerTest(@Autowired MockMvc mockMvc,
                                @Autowired ObjectMapper objectMapper) {
        this.restHitter = mockMvc;
        this.objectMapper = objectMapper;
        this.jsonRestHitter = new JsonRestHitter(restHitter, objectMapper);
    }

    @Test
    @Sql(scripts = "/sql/seed.sql")
    @Order(1)
    void createOneTestSuccessCase() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.CREATE_ONE);
        var createOneAuthorInputDto = new CreateOneAuthorInputDto(
                "John Steward",
                LocalDate.of(1970, 12, 10),
                new ArrayList<>(Arrays.asList(27L))
        );

        Pair<ServiceResponse<CreateOneAuthorOutputDto>, MvcResult> created = this.jsonRestHitter.postRequest(url, createOneAuthorInputDto,
                new TypeReference<ServiceResponse<CreateOneAuthorOutputDto>>() {
                });
        assert(created.second().getResponse().getStatus() == HttpStatus.CREATED.value());
        printMvcResult("CreateOne", created.second());

        ServiceResponse<GetOneAuthorOutputDto> r = getOneById(created.first().data().getId());
        JsonPrinter.prettyPrint(r);
    }

    @Test
    @Order(2)
    void createOneTestFailureCase01() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.CREATE_ONE);
        var createOneAuthorInputDto = new CreateOneAuthorInputDto(
                "John Steward",
                LocalDate.of(1920, 12, 10),
                new ArrayList<>(Arrays.asList(1L, 2L, 3L))
        );
        var mvcResult = this.restHitter.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOneAuthorInputDto))
        ).andExpect(status().isConflict()).andReturn();
        printMvcResult("CreateOne Failure Case 01:", mvcResult);
    }

    @Test
    @Order(3)
    void createManySuccessCase() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.CREATE_MANY);
        CreateOneAuthorInputDto[] createManyInputDto = {
                new CreateOneAuthorInputDto(
                        "Adriana Pineda",
                        LocalDate.of(2000, 12, 10),
                        new ArrayList<>(Arrays.asList(26L))
                ),
                new CreateOneAuthorInputDto(
                        "Tyree Hansen",
                        LocalDate.of(1975, 5, 4),
                        new ArrayList<>(Arrays.asList(25L))
                )
        };

        Pair<ServiceResponse<CreateOneAuthorOutputDto[]>, MvcResult> created = this.jsonRestHitter.postRequest(url, createManyInputDto,
                new TypeReference<ServiceResponse<CreateOneAuthorOutputDto[]>>() {
                });
        assert(created.second().getResponse().getStatus() == HttpStatus.CREATED.value());

        printMvcResult("CreateMany", created.second());

        ServiceResponse<GetOneAuthorOutputDto> firstCreatedAuthor = getOneById(created.first().data()[0].getId());
        ServiceResponse<GetOneAuthorOutputDto> secondCreatedAuthor = getOneById(created.first().data()[1].getId());
        System.out.println("First Created Author:");
        JsonPrinter.prettyPrint(firstCreatedAuthor);


        System.out.println("Second Created Author:");
        JsonPrinter.prettyPrint(secondCreatedAuthor);
    }

    @Test
    @Order(4)
    void createManyFailureCase01() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.CREATE_MANY);
        CreateOneAuthorInputDto[] createManyInputDto = {
                new CreateOneAuthorInputDto(
                        "Jamie Fisher",
                        LocalDate.of(1920, 12, 10),
                        new ArrayList<>(Arrays.asList(35L))
                ),
                new CreateOneAuthorInputDto(
                        "Kennith Franklin",
                        LocalDate.of(1970, 5, 4),
                        new ArrayList<>(Arrays.asList(30L))
                )
        };

        Pair<ServiceResponse<CreateOneAuthorOutputDto[]>, MvcResult> created = this.jsonRestHitter.postRequest(url, createManyInputDto,
                new TypeReference<ServiceResponse<CreateOneAuthorOutputDto[]>>() {
                });

        assert(created.first().isSuccess() == false);
        assert(created.first().errors()[0].equals(new AppError(ErrorCode.InvalidInput, "book id = 35 not found", 35)));
        assert(created.second().getResponse().getStatus() == HttpStatus.CONFLICT.value());

        printMvcResult("createManyFailureCase01", created.second());
    }

    @Test
    @Order(5)
    void getOneTest() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.GET_ONE);
        var condition = """
                {
                  "op": "&&",
                  "lhs": {
                    "op": "=",
                    "lhs": "id",
                    "rhs": 1
                  },
                  "rhs": {
                    "op": "!=",
                    "lhs": "isDeleted",
                    "rhs": true
                  }
                }
                """;

        var mvcResult = this.restHitter.perform(
                get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(condition)
        ).andExpect(status().isOk()).andReturn();
        printMvcResult("getOneTest with condition", mvcResult);
    }

    @Test
    @Order(6)
    void getOneNoResultsTest() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.GET_ONE);
        var condition = """
                {
                  "op": "=",
                  "lhs": "id",
                  "rhs": 5000
                }
                """;

        var mvcResult = this.restHitter.perform(
                get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(condition)
        ).andExpect(status().isBadRequest()).andReturn();
        printMvcResult("getOneNoResultsTest with condition", mvcResult);
    }

    @Test
    @Order(7)
    void getOneByIdTest() throws Exception {
        var url = String.format("%s%s/%d", authorControllerBaseUrl, CrudEndpoints.GET_ONE, 1);
        var mvcResult = this.restHitter.perform(get(url))
                .andExpect(status().isOk()).andReturn();
        printMvcResult("getOneByIdTest", mvcResult);
    }

    ServiceResponse<GetOneAuthorOutputDto> getOneById(Long id) throws Exception {
        var url = String.format("%s%s/%d", authorControllerBaseUrl, CrudEndpoints.GET_ONE, id);
        var responseAsString = this.restHitter.perform(get(url)).andReturn().getResponse().getContentAsString();
        var typeRef = new TypeReference<ServiceResponse<GetOneAuthorOutputDto>>() {
        };
        ServiceResponse<GetOneAuthorOutputDto> hitResult = objectMapper.readValue(responseAsString, typeRef);
        return hitResult;
    }

    @Test
    @Order(8)
    void updateOneTest() throws Exception {
        var authorEntityIdToUpdate = 5L;

        var authorPreUpdate = getOneById(authorEntityIdToUpdate);
        var authorJson = JsonPrinter.toPrettyJson(authorPreUpdate);
        System.out.println("Before Update Author is: ");
        System.out.println(authorJson);
        System.out.println("-------------------------");

        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.UPDATE_ONE);
        var updateOneAuthorInputDto = new UpdateOneAuthorInputDto(
                authorEntityIdToUpdate,
                "Giancarlo Harmon",
                LocalDate.of(1960, 7, 4),
                new ArrayList<>(Arrays.asList(20L, 3L, 6L))
        );

        var mvcResult = this.restHitter.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOneAuthorInputDto))
        ).andExpect(status().isOk()).andReturn();
        printMvcResult("updateOneTest", mvcResult);

        var updatedEntity = getOneById(updateOneAuthorInputDto.getId());
        var entityJson = JsonPrinter.toPrettyJson(updatedEntity);

        System.out.println("After Update final entity is: ");
        System.out.println(entityJson);
    }

    @Test
    @Order(9)
    void updateOneTestFailureCase01() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.UPDATE_ONE);
        var updateOneAuthorInputDto = new UpdateOneAuthorInputDto(
                null,
                "Messi Chancello",
                LocalDate.of(1980, 7, 4),
                new ArrayList<>(Arrays.asList(2L, 3L, 6L))
        );

        var mvcResult = this.restHitter.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOneAuthorInputDto))
        ).andExpect(status().isBadRequest()).andReturn();
        printMvcResult("updateOneTestFailureCase01", mvcResult);
    }

    @Test
    @Order(10)
    void updateManyTest() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.UPDATE_MANY);

        UpdateOneAuthorInputDto[] updateManyAuthorsInputDto = {
                new UpdateOneAuthorInputDto(
                        2L,
                        LocalDate.of(1980, 7, 4),
                        new ArrayList<>(Arrays.asList(7L))
                ),
                new UpdateOneAuthorInputDto(
                        3L,
                        new ArrayList<>(Arrays.asList(10L))
                )
        };

        var mvcResult = this.restHitter.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateManyAuthorsInputDto))
        ).andExpect(status().isOk()).andReturn();
        printMvcResult("updateManyTest", mvcResult);

        for(var updateOneAuthorInputDto: updateManyAuthorsInputDto) {
            var updatedEntity = getOneById(updateOneAuthorInputDto.getId());
            var entityJson = JsonPrinter.toPrettyJson(updatedEntity);
            System.out.println("After Update final entity is: ");
            System.out.println(entityJson);
        }
    }

    /*
        GetMany should respect the Softdelete concept
        it is not supposed to return any soft deleted entity
    */
    @ParameterizedTest
    @ValueSource(longs = { 8 })
    @Order(11)
    public void getManyTest(Long expectedCount) throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneAuthorOutputDto>>> typeRef = new TypeReference<>() {};
        var res = jsonRestHitter.getRequest(url, null, typeRef);
        ServiceResponse<PaginatedResponse<GetOneAuthorOutputDto>> hitResult = res.first();
        assert(res.second().getResponse().getStatus() == HttpStatus.OK.value());
        assert (hitResult.data().data().size() == expectedCount);

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");
    }

    @Test
    @Order(12)
    public void getManyTestWithCondition() throws Exception {
        var condition = """
                {
                  "op": "&&",
                  "lhs": {
                    "op": "<",
                    "lhs": "birthDate",
                    "rhs": "1900-01-01",
                    "type": "Date"
                  },
                  "rhs": {
                    "op": "!=",
                    "lhs": "isDeleted",
                    "rhs": true
                  }
                }
                """;
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneAuthorOutputDto>>> typeRef = new TypeReference<>() {};
        var res = jsonRestHitter.getRequest(url, condition, typeRef);
        ServiceResponse<PaginatedResponse<GetOneAuthorOutputDto>> hitResult = res.first();
        assert(res.second().getResponse().getStatus() == HttpStatus.OK.value());
        assert (hitResult.data().data().size() == 1);

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");
    }

    @Test
    @Order(13)
    void countNoConditionTest() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);

        assertTrue(countsResult.first().isSuccess());
        printMvcResult("countNoConditionTest", countsResult.second());
    }

    private Long countEntities() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);
        return countsResult.first().data();
    }

    @Test
    @Order(14)
    void countWithConditionTest() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.COUNT);
        var condition = """
                {
                    "op": "between",
                    "lhs": "birthDate",
                    "range1": "1880-12-12",
                    "range2": "1950-12-01",
                    "type": "Date"
                }
                """;
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, condition, typeReference);

        assertTrue(countsResult.first().isSuccess());
        assert (countsResult.first().data() == 2);
        printMvcResult("countWithConditionTest", countsResult.second());
    }

    @Test
    @Order(15)
    void deleteOneById() throws Exception {
        var url = String.format("%s%s/%d", authorControllerBaseUrl, CrudEndpoints.DELETE_ONE, 1);
        TypeReference<ServiceResponse<DeleteOneAuthorOutputDto>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<DeleteOneAuthorOutputDto>, MvcResult> countsResult = jsonRestHitter.deleteRequest(url, null, typeReference);

        assertTrue(countsResult.first().isSuccess());
        assert (countsResult.first().data().getId() == 1);
        printMvcResult("deleteOneById", countsResult.second());

        getManyTest(7L);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                {
                    "op": "<=",
                    "lhs": "birthDate",
                    "rhs": "1950-12-30",
                    "type": "Date"
                }
                """
    })
    @Order(16)
    void deleteMany(String condition) throws Exception {
        var countBeforeDeletion = countEntities();
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.DELETE_MANY);
        TypeReference<ServiceResponse<List<DeleteOneAuthorOutputDto>>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<List<DeleteOneAuthorOutputDto>>, MvcResult> deleteManyResult = jsonRestHitter.deleteRequest(url, condition, typeReference);
        assertTrue(deleteManyResult.first().isSuccess());
        printMvcResult("deleteMany", deleteManyResult.second());

        System.out.printf("Count before deletion: %d\n", countBeforeDeletion);

        Long countOfDeleted = (long)deleteManyResult.first().data().size();
        Long countAfterDeletion = countBeforeDeletion - countOfDeleted;

        System.out.printf("Count of deleted: %d\n", countOfDeleted);
        System.out.printf("Count after deletion: %d\n", countAfterDeletion);

        getManyTest(countAfterDeletion);
    }

    private static void printMvcResult(String title, MvcResult mvcResult) throws Exception {
        var body = mvcResult.getRequest().getContentAsString();
        System.out.println("");
        System.out.printf("================ %s =================================\n", title);
        System.out.printf("url: %s\n", mvcResult.getRequest().getRequestURI());
        System.out.printf("status code: %d\n", mvcResult.getResponse().getStatus());
        if (!StringExtensions.IsNullOrEmpty(body)) {
            System.out.printf("request body: %s\n", body);
        }
        System.out.printf("response body: %s\n", mvcResult.getResponse().getContentAsString());
        System.out.println("=================================================\n");
    }
}