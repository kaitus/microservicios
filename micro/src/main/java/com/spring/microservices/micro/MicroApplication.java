package com.spring.microservices.micro;

import com.spring.microservices.micro.api.RestFullVerticle;
import com.spring.microservices.micro.verticles.ServicesVerticle;
import io.vertx.core.Vertx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MicroApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroApplication.class, args);
    }

    @PostConstruct
    void deployVerticle() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ServicesVerticle.class.getName());
        vertx.deployVerticle(RestFullVerticle.class.getName());
    }

}
