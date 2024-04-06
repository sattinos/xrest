package org.malsati.controllers_test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.malsati.simple_web_app.dto.author.CreateOneAuthorInputDto;
import org.malsati.simple_web_app.dto.author.CreateOneAuthorOutputDto;
import org.malsati.simple_web_app.dto.author.DeleteOneAuthorOutputDto;
import org.malsati.simple_web_app.dto.author.GetOneAuthorOutputDto;
import org.malsati.simple_web_app.dto.book.CreateOneBookInputDto;
import org.malsati.simple_web_app.dto.book.CreateOneBookOutputDto;
import org.malsati.simple_web_app.utils.json.JsonPrinter;
import org.malsati.utilities.LogHelper;
import org.malsati.utilities.json.JsonRestHitter;
import org.malsati.xrest.controller.CrudEndpoints;
import org.malsati.xrest.dto.ServiceResponse;
import org.malsati.xrest.dto.pagination.PaginatedResponse;
import org.malsati.xrest.utilities.tuples.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class T03_AuthorAndBookControllersTest {
    private static final String authorControllerBaseUrl = "/app/author";
    private static final String bookControllerBaseUrl = "/app/book";

    private MockMvc restHitter;
    private JsonRestHitter jsonRestHitter;

    private ObjectMapper objectMapper;

    public T03_AuthorAndBookControllersTest(@Autowired MockMvc mockMvc,
                                    @Autowired ObjectMapper objectMapper) {
        this.restHitter = mockMvc;
        this.objectMapper = objectMapper;
        this.jsonRestHitter = new JsonRestHitter(restHitter, objectMapper);
    }

    @Test
    @Order(1)
    void createManyAuthors() throws Exception {
        File resource = new ClassPathResource("json/authors.json").getFile();
        List<CreateOneAuthorInputDto> createManyInputDto = objectMapper.readValue(resource, new TypeReference<List<CreateOneAuthorInputDto>>(){});

        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.CREATE_MANY);

        Pair<ServiceResponse<CreateOneAuthorOutputDto[]>, MvcResult> created = this.jsonRestHitter.postRequest(url, createManyInputDto,
                new TypeReference<ServiceResponse<CreateOneAuthorOutputDto[]>>() {
                });
        assert(created.second().getResponse().getStatus() == HttpStatus.CREATED.value());

        LogHelper.printMvcResult("CreateMany Authors", created.second());

        System.out.println("All Authors: ");
        JsonPrinter.prettyPrint(created.first());
    }

    @Test
    @Order(2)
    void createManyBooks() throws Exception {
        File resource = new ClassPathResource("json/books.json").getFile();
        List<CreateOneBookInputDto> createManyInputDto = objectMapper.readValue(resource, new TypeReference<List<CreateOneBookInputDto>>(){});

        var url = String.format("%s%s", bookControllerBaseUrl, CrudEndpoints.CREATE_MANY);
        Pair<ServiceResponse<CreateOneBookOutputDto[]>, MvcResult> created = this.jsonRestHitter.postRequest(url, createManyInputDto,
                new TypeReference<ServiceResponse<CreateOneBookOutputDto[]>>() {
                });
        assert (created.second().getResponse().getStatus() == HttpStatus.CREATED.value());

        LogHelper.printMvcResult("CreateMany Books", created.second());
        System.out.println("All Books: ");
        JsonPrinter.prettyPrint(created.first());
    }

    @Test
    @Order(3)
    void deleteAuthorsOfSpecificBook() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.DELETE_MANY);
        // Delete all the authors who authored the book: "Artificial Intelligence"
        var condition = """
                {
                    "op": "=",
                    "lhs": "books.title",
                    "rhs": "Artificial Intelligence"
                }
                """;


        var countBeforeDeletion = countEntities();
        TypeReference<ServiceResponse<List<DeleteOneAuthorOutputDto>>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<List<DeleteOneAuthorOutputDto>>, MvcResult> deleteManyResult = jsonRestHitter.deleteRequest(url, condition, typeReference);
        assertTrue(deleteManyResult.first().isSuccess());
        LogHelper.printMvcResult("deleteMany", deleteManyResult.second());

        System.out.printf("Count before deletion: %d\n", countBeforeDeletion);

        Long countOfDeleted = (long)deleteManyResult.first().data().size();
        Long countAfterDeletion = countBeforeDeletion - countOfDeleted;

        System.out.printf("Count of deleted: %d\n", countOfDeleted);
        System.out.printf("Count after deletion: %d\n", countAfterDeletion);


        var allAuthors = getAllAuthors().data();
        for (var author: allAuthors) {
            for (var book: author.getBooks()) {
                Assertions.assertThat(book.getTitle()).isNotEqualToNormalizingWhitespace("Artificial Intelligence");
            }
        }
    }

    public PaginatedResponse<GetOneAuthorOutputDto> getAllAuthors() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.GET_MANY);
        TypeReference<ServiceResponse<PaginatedResponse<GetOneAuthorOutputDto>>> typeRef = new TypeReference<>() {};
        var res = jsonRestHitter.getRequest(url, null, typeRef);
        ServiceResponse<PaginatedResponse<GetOneAuthorOutputDto>> hitResult = res.first();
        return hitResult.data();
    }

    private Long countEntities() throws Exception {
        var url = String.format("%s%s", authorControllerBaseUrl, CrudEndpoints.COUNT);
        TypeReference<ServiceResponse<Long>> typeReference = new TypeReference<>() {};
        Pair<ServiceResponse<Long>, MvcResult> countsResult = jsonRestHitter.getRequest(url, null, typeReference);
        return countsResult.first().data();
    }
}
