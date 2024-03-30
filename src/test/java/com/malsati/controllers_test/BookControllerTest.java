package com.malsati.controllers_test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malsati.simple_web_app.dto.book.*;
import com.malsati.simple_web_app.infrastructure.BookRepository;
import com.malsati.simple_web_app.utils.json.JsonPrinter;
import com.malsati.utilities.LogHelper;
import com.malsati.utilities.json.JsonRestHitter;
import com.malsati.xrest.controller.CrudEndpoints;
import com.malsati.xrest.dto.ServiceResponse;
import com.malsati.xrest.dto.pagination.PaginatedResponse;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerTest {
    private static final String bookControllerBaseUrl = "/app/book";

    @Autowired
    private BookRepository bookRepository;

    private MockMvc restHitter;
    private JsonRestHitter jsonRestHitter;
    private ObjectMapper objectMapper;

    public BookControllerTest(@Autowired MockMvc mockMvc,
                              @Autowired ObjectMapper objectMapper) {
        this.restHitter = mockMvc;
        this.objectMapper = objectMapper;
        this.jsonRestHitter = new JsonRestHitter(restHitter, objectMapper);
    }

    @Test
    @Order(1)
    void createOneTestSuccessCase() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.CREATE_ONE);
        var createOneBookInputDto = new CreateOneBookInputDto(
                "Algorithms",
                LocalDate.of(2010, 9, 14),
                3,
                1,
                "APress",
                670);

        Pair<ServiceResponse<CreateOneBookOutputDto>, MvcResult> created = this.jsonRestHitter.postRequest(url, createOneBookInputDto,
                new TypeReference<ServiceResponse<CreateOneBookOutputDto>>() {
                });
        assert(created.second().getResponse().getStatus() == HttpStatus.CREATED.value());
        LogHelper.printMvcResult("CreateOne", created.second());

        ServiceResponse<GetOneBookOutputDto> r = getOneById(created.first().data().getId());
        JsonPrinter.prettyPrint(r);
    }

    @Test
    @Order(3)
    void createManySuccessCase() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.CREATE_MANY);
        CreateOneBookInputDto[] createManyInputDto = {
                new CreateOneBookInputDto(
                        "Game of Thrones 1",
                        LocalDate.of(2000, 12, 20),
                        1,
                        1,
                        "HBC",
                        390
                ),
                new CreateOneBookInputDto(
                        "Game of Thrones 2",
                        LocalDate.of(2000, 12, 15),
                        2,
                        1,
                        "HBC",
                        200
                )
        };

        Pair<ServiceResponse<CreateOneBookOutputDto[]>, MvcResult> created = this.jsonRestHitter.postRequest(url, createManyInputDto,
                new TypeReference<ServiceResponse<CreateOneBookOutputDto[]>>() {
                });
        assert(created.second().getResponse().getStatus() == HttpStatus.CREATED.value());

        LogHelper.printMvcResult("CreateMany", created.second());
        ServiceResponse<GetOneBookOutputDto> firstCreatedAuthor = getOneById(created.first().data()[0].getId());
        ServiceResponse<GetOneBookOutputDto> secondCreatedAuthor = getOneById(created.first().data()[1].getId());
        System.out.println("First Created Author:");
        JsonPrinter.prettyPrint(firstCreatedAuthor);


        System.out.println("Second Created Author:");
        JsonPrinter.prettyPrint(secondCreatedAuthor);
    }

    @Test
    @Order(4)
    void getOneTest() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.GET_ONE);
        var condition = """
               {
                    "op": "=",
                    "lhs": "id",
                    "rhs": 1
               }
               """;
        var mvcResult = this.restHitter.perform(
                         get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(condition)
        ).andExpect(status().isOk()).andReturn();
        LogHelper.printMvcResult("getOneTest with condition", mvcResult);
    }

    @Test
    @Order(5)
    void getOneByIdTest() throws Exception {
        var url = String.format("%s%s/%d", bookControllerBaseUrl, CrudEndpoints.GET_ONE, 1);
        var mvcResult = this.restHitter.perform(get(url))
                .andExpect(status().isOk()).andReturn();
        LogHelper.printMvcResult("getOneByIdTest", mvcResult);
    }

    ServiceResponse<GetOneBookOutputDto> getOneById(Long id) throws Exception {
        var url = String.format("%s%s/%d", bookControllerBaseUrl, CrudEndpoints.GET_ONE, id);
        var responseAsString = this.restHitter.perform(get(url)).andReturn().getResponse().getContentAsString();
        var typeRef = new TypeReference<ServiceResponse<GetOneBookOutputDto>>() {
        };
        ServiceResponse<GetOneBookOutputDto> hitResult = objectMapper.readValue(responseAsString, typeRef);
        return hitResult;
    }

    @Test
    @Order(6)
    void updateOneTest() throws Exception {
        var bookEntityIdToUpdate = 2L;
        var bookPreUpdate = getOneById(bookEntityIdToUpdate);
        var bookJson = JsonPrinter.toPrettyJson(bookPreUpdate);

        System.out.println("Before Update Book is: ");
        System.out.println(bookJson);
        System.out.println("-------------------------");

        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.UPDATE_ONE);
        var updateOneBookInputDto = new UpdateOneBookInputDto(
                "Rise Of The Dragons",
                3,
                2,
                bookEntityIdToUpdate
        );

        var mvcResult = this.restHitter.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOneBookInputDto))
        ).andExpect(status().isOk()).andReturn();
        LogHelper.printMvcResult("updateOneTest", mvcResult);

        var updatedEntity = getOneById(updateOneBookInputDto.getId());

        var entityJson = JsonPrinter.toPrettyJson(updatedEntity);
        System.out.println("After Update final book is: ");
        System.out.println(entityJson);

        assert (updatedEntity.data().getTitle().contentEquals(updateOneBookInputDto.getTitle()));
        assert (updatedEntity.data().getEdition().equals(updateOneBookInputDto.getEdition()));
        assert (updatedEntity.data().getVolume().equals(updateOneBookInputDto.getVolume()));
    }



    @Test
    @Order(7)
    void updateManyTest() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.UPDATE_MANY);

        UpdateOneBookInputDto[] updateManyAuthorsInputDto = {
                new UpdateOneBookInputDto("The Eathquake", 4, 5, 3L),
                new UpdateOneBookInputDto("The Edge Of Darkness", 2, 2, 1L)
        };

        var mvcResult = this.restHitter.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateManyAuthorsInputDto))
        ).andExpect(status().isOk()).andReturn();
        LogHelper.printMvcResult("updateManyTest", mvcResult);

        for(var updateOneAuthorInputDto: updateManyAuthorsInputDto) {
            var updatedEntity = getOneById(updateOneAuthorInputDto.getId());
            var entityJson = JsonPrinter.toPrettyJson(updatedEntity);
            System.out.println("After Update final entity is: ");
            System.out.println(entityJson);
        }
    }
    
    @ParameterizedTest
    @ValueSource(longs = { 3 })
    @Order(8)
    public void getManyTest(Long expectedCount) throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneBookOutputDto>>> typeRef = new TypeReference<>() {};
        var res = jsonRestHitter.getRequest(url, null, typeRef);
        ServiceResponse<PaginatedResponse<GetOneBookOutputDto>> hitResult = res.first();

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");

        assert(res.second().getResponse().getStatus() == HttpStatus.OK.value());
        assertEquals (expectedCount, hitResult.data().data().size());
    }

    @Test
    @Order(9)
    public void getManyTestWithCondition() throws Exception {
        var condition = """
                {
                  "op": "&&",
                  "lhs": {
                    "op": "<",
                    "lhs": "publishDate",
                    "rhs": "2000-12-25",
                    "type": "Date"
                  },
                  "rhs": {
                    "op": "<",
                    "lhs": "noPages",
                    "rhs": 300
                  }
                }
                """;
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneBookOutputDto>>> typeRef = new TypeReference<>() {};
        var res = jsonRestHitter.getRequest(url, condition, typeRef);
        ServiceResponse<PaginatedResponse<GetOneBookOutputDto>> hitResult = res.first();

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");

        assert(res.second().getResponse().getStatus() == HttpStatus.OK.value());
        assertEquals (1, hitResult.data().data().size());
    }

    @Test
    @Order(10)
    void countNoConditionTest() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);

        assertTrue(countsResult.first().isSuccess());
        LogHelper.printMvcResult("countNoConditionTest", countsResult.second());
    }

    private Long countEntities() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);
        return countsResult.first().data();
    }

    @Test
    @Order(11)
    void countWithConditionTest() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.COUNT);
        var condition = """
                {
                    "op": "between",
                    "lhs": "publishDate",
                    "range1": "1900-12-12",
                    "range2": "2010-12-01",
                    "type": "Date"
                }
                """;
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, condition, typeReference);

        assertTrue(countsResult.first().isSuccess());
        assertEquals (3, countsResult.first().data());
        LogHelper.printMvcResult("countWithConditionTest", countsResult.second());
    }

    @Test
    @Order(15)
    void deleteOneById() throws Exception {
        var countBeforeDeletion = countEntities();
        System.out.printf("Count before deletion: %d\n", countBeforeDeletion);

        var url = String.format("%s%s/%d", bookControllerBaseUrl, CrudEndpoints.DELETE_ONE, 1);
        TypeReference<ServiceResponse<DeleteOneBookOutputDto>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<DeleteOneBookOutputDto>, MvcResult> countsResult = jsonRestHitter.deleteRequest(url, null, typeReference);

        LogHelper.printMvcResult("deleteOneById", countsResult.second());

        assertTrue(countsResult.first().isSuccess());
        assertEquals (countsResult.first().data().getId(), 1L);

        getManyTest(countBeforeDeletion - 1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                {
                    "op": "<=",
                    "lhs": "publishDate",
                    "rhs": "2000-12-18",
                    "type": "Date"
                }
                """
    })
    @Order(16)
    void deleteMany(String condition) throws Exception {
        var countBeforeDeletion = countEntities();
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.DELETE_MANY);
        TypeReference<ServiceResponse<List<DeleteOneBookOutputDto>>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<List<DeleteOneBookOutputDto>>, MvcResult> deleteManyResult = jsonRestHitter.deleteRequest(url, condition, typeReference);
        assertTrue(deleteManyResult.first().isSuccess());
        LogHelper.printMvcResult("deleteMany", deleteManyResult.second());

        System.out.printf("Count before deletion: %d\n", countBeforeDeletion);

        Long countOfDeleted = (long)deleteManyResult.first().data().size();
        Long countAfterDeletion = countBeforeDeletion - countOfDeleted;

        System.out.printf("Count of deleted: %d\n", countOfDeleted);
        System.out.printf("Count after deletion: %d\n", countAfterDeletion);

        getManyTest(countAfterDeletion);
    }
}

