package org.malsati.controllers_test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.malsati.simple_web_app.dto.book.*;
import org.malsati.simple_web_app.dto.book.*;
import org.malsati.simple_web_app.infrastructure.BookRepository;
import org.malsati.simple_web_app.utils.json.JsonPrinter;
import org.malsati.utilities.LogHelper;
import org.malsati.utilities.json.JsonRestHitter;
import org.malsati.xrest.controller.CrudEndpoints;
import org.malsati.xrest.dto.ServiceResponse;
import org.malsati.xrest.dto.pagination.PaginatedResponse;
import org.malsati.xrest.utilities.tuples.Pair;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.assertj.core.api.Assertions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class T02_BookControllerTest {
    private static final String bookControllerBaseUrl = "/app/book";


    @Autowired
    private BookRepository bookRepository;

    private final MockMvc restHitter;
    private final JsonRestHitter jsonRestHitter;
    private final ObjectMapper objectMapper;

    public T02_BookControllerTest(@Autowired MockMvc mockMvc,
                                  @Autowired ObjectMapper objectMapper) {
        this.restHitter = mockMvc;
        this.objectMapper = objectMapper;
        this.jsonRestHitter = new JsonRestHitter(restHitter, objectMapper);
    }

    private static final ArrayList<GetOneBookOutputDto> createdBooks = new ArrayList<>();

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
                670,
                new ArrayList<>(List.of(1L, 2L)));

        Pair<ServiceResponse<CreateOneBookOutputDto>, MvcResult> created = this.jsonRestHitter.postRequest(url, createOneBookInputDto,
                new TypeReference<ServiceResponse<CreateOneBookOutputDto>>() {
                });
        assert (created.second().getResponse().getStatus() == HttpStatus.CREATED.value());
        LogHelper.printMvcResult("CreateOne", created.second());

        ServiceResponse<GetOneBookOutputDto> createdBookOutputDto = getOneById(created.first().data().getId());
        JsonPrinter.prettyPrint(createdBookOutputDto);

        createdBooks.add(createdBookOutputDto.data());
    }

    @Test
    @Order(2)
    void createManySuccessCase() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.CREATE_MANY);
        CreateOneBookInputDto[] createManyInputDto = {
                new CreateOneBookInputDto(
                        "Game of Thrones 1",
                        LocalDate.of(2000, 12, 20),
                        1,
                        1,
                        "HBC",
                        390,
                        new ArrayList<>(List.of(1L, 2L))
                ),
                new CreateOneBookInputDto(
                        "Game of Thrones 2",
                        LocalDate.of(2000, 12, 15),
                        2,
                        1,
                        "HBC",
                        200,
                        new ArrayList<>(List.of(1L, 2L))
                )
        };

        Pair<ServiceResponse<CreateOneBookOutputDto[]>, MvcResult> created = this.jsonRestHitter.postRequest(url, createManyInputDto,
                new TypeReference<ServiceResponse<CreateOneBookOutputDto[]>>() {
                });
        assert (created.second().getResponse().getStatus() == HttpStatus.CREATED.value());

        LogHelper.printMvcResult("CreateMany", created.second());
        ServiceResponse<GetOneBookOutputDto> firstCreatedBook = getOneById(created.first().data()[0].getId());
        ServiceResponse<GetOneBookOutputDto> secondCreatedBook = getOneById(created.first().data()[1].getId());
        System.out.println("First Created Book:");
        JsonPrinter.prettyPrint(firstCreatedBook);

        System.out.println("Second Created Book:");
        JsonPrinter.prettyPrint(secondCreatedBook);

        createdBooks.add(firstCreatedBook.data());
        createdBooks.add(secondCreatedBook.data());
    }

    @Test
    @Order(3)
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
    @Order(4)
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
    @Order(5)
    // @Transactional    @Rollback(value = false)
    void updateOneTest() throws Exception {
        var bookJson = JsonPrinter.toPrettyJson(createdBooks.get(0));

        System.out.println("Before Update Book is: ");
        System.out.println(bookJson);
        System.out.println("-------------------------");

        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.UPDATE_ONE);
        var updateOneBookInputDto = new UpdateOneBookInputDto(
                "Rise Of The Dragons",
                3,
                2,
                createdBooks.get(0).getId(),
                new ArrayList<>(List.of(4L, 5L, 6L))
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

        var authorsIds = updatedEntity.data().getAuthors().stream().map(authors -> authors.id()).collect(Collectors.toList());

        Assertions.assertThat(authorsIds).isEqualTo(updateOneBookInputDto.getAuthorsIds());
    }


    @Test
    @Order(6)
    void updateManyTest() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.UPDATE_MANY);

        UpdateOneBookInputDto[] updateManyBooksInputDto = {
                new UpdateOneBookInputDto("The Eathquake", 4, 5, createdBooks.get(0).getId()),
                new UpdateOneBookInputDto("The Edge Of Darkness", 2, 2, createdBooks.get(1).getId())
        };

        var mvcResult = this.restHitter.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateManyBooksInputDto))
        ).andExpect(status().isOk()).andReturn();
        LogHelper.printMvcResult("updateManyTest", mvcResult);

        for (var updateOneBookInputDto : updateManyBooksInputDto) {
            var updatedEntity = getOneById(updateOneBookInputDto.getId());
            var entityJson = JsonPrinter.toPrettyJson(updatedEntity);
            System.out.println("After Update final entity is: ");
            System.out.println(entityJson);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {13})
    @Order(7)
    public void getManyTest(int expectedCount) throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneBookOutputDto>>> typeRef = new TypeReference<>() {
        };
        var res = jsonRestHitter.getRequest(url, null, typeRef);
        ServiceResponse<PaginatedResponse<GetOneBookOutputDto>> hitResult = res.first();

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");

        assert (res.second().getResponse().getStatus() == HttpStatus.OK.value());

        Assertions.assertThat(hitResult.data().data().size()).isEqualTo(expectedCount);
    }

    @Test
    @Order(8)
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
        TypeReference<ServiceResponse<PaginatedResponse<GetOneBookOutputDto>>> typeRef = new TypeReference<>() {
        };
        var res = jsonRestHitter.getRequest(url, condition, typeRef);
        ServiceResponse<PaginatedResponse<GetOneBookOutputDto>> hitResult = res.first();

        LogHelper.printMvcResult("GetManyTest with condition", res.second());

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");

        assert (res.second().getResponse().getStatus() == HttpStatus.OK.value());
        Assertions.assertThat(hitResult.data().data().size()).isEqualTo(4);
    }

    @Test
    @Order(9)
    void countNoConditionTest() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {
        };
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);

        Assertions.assertThat(countsResult.first().isSuccess()).isEqualTo(true);
        LogHelper.printMvcResult("countNoConditionTest", countsResult.second());
    }

    private int countEntities() throws Exception {
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Integer>> typeReference = new TypeReference<>() {
        };
        Pair<ServiceResponse<Integer>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);
        return countsResult.first().data();
    }

    @Test
    @Order(10)
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
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {
        };
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, condition, typeReference);

        Assertions.assertThat(countsResult.first().isSuccess()).isEqualTo(true);
        Assertions.assertThat(countsResult.first().data()).isEqualTo(13);
        LogHelper.printMvcResult("countWithConditionTest", countsResult.second());
    }

    @Test
    @Order(11)
    void deleteOneById() throws Exception {
        /*
            Create 2 Authors
        */

        var countBeforeDeletion = countEntities();
        System.out.printf("Count before deletion: %d\n", countBeforeDeletion);

        var url = String.format("%s%s/%d", bookControllerBaseUrl, CrudEndpoints.DELETE_ONE, createdBooks.get(0).getId());
        TypeReference<ServiceResponse<DeleteOneBookOutputDto>> typeReference = new TypeReference<>() {
        };
        Pair<ServiceResponse<DeleteOneBookOutputDto>, MvcResult> countsResult = jsonRestHitter.deleteRequest(url, null, typeReference);

        LogHelper.printMvcResult("deleteOneById", countsResult.second());

        Assertions.assertThat(countsResult.first().isSuccess()).isEqualTo(true);
        Assertions.assertThat((Long)countsResult.first().data().getId()).isEqualTo((Long)createdBooks.get(0).getId());

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
    @Order(12)
    void deleteMany(String condition) throws Exception {
        var countBeforeDeletion = countEntities();
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.DELETE_MANY);
        TypeReference<ServiceResponse<List<DeleteOneBookOutputDto>>> typeReference = new TypeReference<>() {
        };
        Pair<ServiceResponse<List<DeleteOneBookOutputDto>>, MvcResult> deleteManyResult = jsonRestHitter.deleteRequest(url, condition, typeReference);

        Assertions.assertThat(deleteManyResult.first().isSuccess()).isEqualTo(true);

        LogHelper.printMvcResult("deleteMany", deleteManyResult.second());

        System.out.printf("Count before deletion: %d\n", countBeforeDeletion);

        Integer countOfDeleted = deleteManyResult.first().data().size();
        Integer countAfterDeletion = countBeforeDeletion - countOfDeleted;

        System.out.printf("Count of deleted: %d\n", countOfDeleted);
        System.out.printf("Count after deletion: %d\n", countAfterDeletion);

        getManyTest(countAfterDeletion);
    }

    @Test
    @Order(13)
    public void getManyTestWithConditionQueryRelatedObjects() throws Exception {
        var condition = """
                    {
                    "op": "=",
                    "lhs": "authors.id",
                    "rhs": 1
                  }
                """;
        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneBookOutputDto>>> typeRef = new TypeReference<>() {
        };
        var res = jsonRestHitter.getRequest(url, condition, typeRef);
        ServiceResponse<PaginatedResponse<GetOneBookOutputDto>> hitResult = res.first();

        System.out.println("-----------");
        System.out.println("Get Many Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(hitResult);
        System.out.println("-----------");

        Assertions.assertThat(res.second().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(hitResult.data().data().size()).isEqualTo(1);
    }
}