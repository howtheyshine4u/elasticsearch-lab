package com.tanghs.elasticsearchlab;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Configurable
public class ElasticsearchLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchLabApplication.class, args);
    }

}
