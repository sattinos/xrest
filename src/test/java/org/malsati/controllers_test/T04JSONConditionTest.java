package org.malsati.controllers_test;

import org.junit.jupiter.api.*;
import org.malsati.simple_web_app.dto.book.CreateOneBookInputDto;
import org.malsati.simple_web_app.dto.book.CreateOneBookOutputDto;
import org.malsati.simple_web_app.service.BookService;
import org.malsati.xrest.dto.ServiceResponse;
import org.malsati.xrest.utilities.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class T04JSONConditionTest {

    public T04JSONConditionTest(@Autowired BookService bookService) {
        this.bookService = bookService;
    }

    private final BookService bookService;

    private static final List<Pair<String, Long>> conditions = List.of(
            new Pair<>(
            """
                {
                  "op": "in",
                  "lhs": "id",
                  "rhs": "{}"
                }
            """, 3L),
            new Pair<>(
            """
                {
                  "op": "in",
                  "lhs": "title",
                  "rhs": "The Success Routine, The Planet Heroes"
                }
            """, 2L),
            new Pair<>(
            """
                {
                  "op": "in",
                  "lhs": "publishDate",
                  "rhs": "2005-12-20, 1982-08-25",
                  "type": "Date"
                }
            """, 2L)
    );

    @BeforeAll
    public void createManyBooks() {
        CreateOneBookInputDto[] createManyInputDto = {
                new CreateOneBookInputDto(
                        "The Success Routine",
                        LocalDate.of(2005, 12, 20),
                        1,
                        1,
                        "TreeHouse",
                        390,
                        new ArrayList<>(List.of(1L, 2L))
                ),
                new CreateOneBookInputDto(
                        "The Planet Heroes",
                        LocalDate.of(1982, 8, 25),
                        2,
                        1,
                        "TreeHouse",
                        200,
                        new ArrayList<>(List.of(1L, 2L))
                ),
                new CreateOneBookInputDto(
                        "The Planet Savors",
                        LocalDate.of(1963, 7, 15),
                        3,
                        2,
                        "TreeHouse",
                        600,
                        new ArrayList<>(List.of(1L, 2L))
                )
        };

        ServiceResponse<List<CreateOneBookOutputDto>> result = bookService.createMany(List.of(createManyInputDto));

        assert (result.isSuccess());
        assert (result.data().size() == 3);

        createdBooks = result.data();
    }

    private List<CreateOneBookOutputDto> createdBooks;

    @Test
    @Order(1)
    @DisplayName("In Operator test: Integers case")
    void inOperatorIntegerCase() {
        String condition = conditions.get(0).first();

        String ids = createdBooks.stream().map(CreateOneBookOutputDto::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        var bakedCondition = condition.replace("{}", ids);

        System.out.printf("bakedCondition: %s\n", bakedCondition);
        var serviceResponse = bookService.count(bakedCondition);
        System.out.printf("Service Response: %d\n", serviceResponse.data());

        assert (serviceResponse.isSuccess());
        assert (serviceResponse.data().equals(conditions.get(0).second()));
    }

    @Test
    @Order(2)
    @DisplayName("In Operator test: Strings case")
    void inOperatorStringCase() {
        String condition = conditions.get(1).first();

        System.out.printf("Condition: %s\n", condition);
        var serviceResponse = bookService.count(condition);
        System.out.printf("Service Response: %d\n", serviceResponse.data());

        assert (serviceResponse.isSuccess());
        assert (serviceResponse.data().equals(conditions.get(1).second()));
    }

    @Test
    @Order(3)
    @DisplayName("In Operator test: Dates case")
    void inOperatorDatesCase() {
        String condition = conditions.get(2).first();

        System.out.printf("Condition: %s\n", condition);
        var serviceResponse = bookService.count(condition);
        System.out.printf("Service Response: %d\n", serviceResponse.data());

        assert (serviceResponse.isSuccess());
        assert (Objects.equals(serviceResponse.data(), conditions.get(2).second()));
    }
}
