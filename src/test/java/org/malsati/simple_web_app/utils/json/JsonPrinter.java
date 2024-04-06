package org.malsati.simple_web_app.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonPrinter {
    public static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static void prettyPrint(Object obj)  {
        objectMapper.findAndRegisterModules();
        String serialized = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        System.out.println(serialized);
    }

    @SneakyThrows
    public static String toPrettyJson(Object obj) {
        objectMapper.findAndRegisterModules();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}