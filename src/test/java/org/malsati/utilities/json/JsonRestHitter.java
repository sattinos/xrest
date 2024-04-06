package org.malsati.utilities.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.malsati.xrest.utilities.tuples.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class JsonRestHitter {
    public JsonRestHitter(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    public <T, U> Pair<U, MvcResult> postRequest(String url, T body, TypeReference<U> typeRef) throws Exception {
        var mvcResult = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        ).andReturn();

        U res = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), typeRef);
        return new Pair<>(res, mvcResult);
    }

    public <T, U> Pair<U, MvcResult> getRequest(String url, T body, TypeReference<U> typeRef) throws Exception {
        MvcResult mvcResult = null;
        if( body == null) {
            mvcResult = mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andReturn();
        }
        if( body != null) {
            String bodyAsString = body.getClass() == String.class ? (String)body : objectMapper.writeValueAsString(body);
            mvcResult = mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyAsString)
            ).andReturn();
        }
        U res = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), typeRef);
        return new Pair<>(res, mvcResult);
    }

    public <T, U> Pair<U, MvcResult> deleteRequest(String url, T body, TypeReference<U> typeRef) throws Exception {
        MvcResult mvcResult = null;
        if( body == null) {
            mvcResult = mockMvc.perform(delete(url)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andReturn();
        }
        if( body != null) {
            String bodyAsString = body.getClass() == String.class ? (String)body : objectMapper.writeValueAsString(body);
            mvcResult = mockMvc.perform(delete(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyAsString)
            ).andReturn();
        }
        U res = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), typeRef);
        return new Pair<>(res, mvcResult);
    }
}