package com.spring.microservices.micro.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.microservices.micro.verticles.ServicesVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


public class RestFullVerticle extends AbstractVerticle {

    private final ObjectMapper mapper = Json.mapper;

    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);
        router.get("/api/:collection").handler(this::findAllHandler);
        router.post("/api/:collection").handler(this::createHandler);
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, httpServerAsyncResult -> {
                    if (httpServerAsyncResult.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(httpServerAsyncResult.cause());
                    }
                });
    }

    private void findAllHandler(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "findAll");
        JsonObject object = new JsonObject().put("collection", routingContext.request().getParam("collection"));
        reply(routingContext, options, object);
    }

    private void createHandler(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "create");
        JsonObject object = routingContext.getBodyAsJson();
        object.put("collection", routingContext.request().getParam("collection"));
        reply(routingContext, options, object);
    }

    private void reply(RoutingContext routingContext, DeliveryOptions options, JsonObject object) {
        vertx.eventBus().<JsonObject>send(ServicesVerticle.class.getName(), object, options, messageAsyncResult -> {
            if (messageAsyncResult.succeeded()) {
                try {
                    routingContext.response().end(mapper.writeValueAsString(messageAsyncResult.result()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                routingContext.response().end(messageAsyncResult.cause().getMessage());
            }
        });
    }
}
