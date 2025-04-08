package com.example.final_project_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
public class FinalProjectBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectBeApplication.class, args);
    }

}
