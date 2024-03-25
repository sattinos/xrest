package com.malsati.repositories_test;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.Test;
import lombok.SneakyThrows;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.malsati.simple_web_app.infrastructure.BookRepository;
import com.malsati.simple_web_app.utils.json.JsonPrinter;
import com.malsati.xrest.infrastructure.jpql.SpecificationBuilder;

@SpringBootTest
class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    SpecificationBuilder specificationBuilder;

    @Test
    @Sql(scripts = "/sql/seed.sql")
    void shouldBeNotNull() throws JsonProcessingException {
        assert (specificationBuilder != null);
        /*
            We need Harry Potter books that were published between 1999-06-01 -> 2003-12-01
        */
        var condition = loadTestCase(6);

        var res = executeCondition(condition, bookRepository);

        System.out.println("-----------");
        System.out.println("Condition:");
        System.out.println(condition);
        System.out.println("-----------");
        System.out.println("Find Result:");
        System.out.println("-----------");
        JsonPrinter.prettyPrint(res);
        System.out.println("-----------");

        System.out.printf("No Results: %d\n", res.size());
        System.out.println("-----------");
        assert(res.size() == 3);
    }

    @SneakyThrows
    String loadTestCase(int caseIndex) {
        ClassPathResource resource = new ClassPathResource(String.format("conditions/condition%d.json", caseIndex));
        InputStream inputStream = resource.getInputStream();
        String condition = new String(inputStream.readAllBytes());
        inputStream.close();
        return condition;
    }

    <T> List<T> executeCondition(String condition, JpaSpecificationExecutor<T> repository) {
        var criteria = specificationBuilder.build(condition);
        return repository.findAll(criteria);
    }
}