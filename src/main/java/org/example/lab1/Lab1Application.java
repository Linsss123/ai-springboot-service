package org.example.lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class Lab1Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab1Application.class, args);
    }

}
