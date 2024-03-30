package com.malsati.utilities;

import com.malsati.xrest.utilities.text.StringExtensions;
import org.springframework.test.web.servlet.MvcResult;

public class LogHelper {
    public static void printMvcResult(String title, MvcResult mvcResult) throws Exception {
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
