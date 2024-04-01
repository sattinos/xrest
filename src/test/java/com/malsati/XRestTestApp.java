package com.malsati;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class XRestTestApp {
    public static void main(String[] args) {
        SpringApplication.run(XRestTestApp.class, args);
    }
}
