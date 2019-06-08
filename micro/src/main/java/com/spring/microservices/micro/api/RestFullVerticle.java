package com.spring.microservices.micro.api;

import com.spring.microservices.micro.verticles.ServicesVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


public class RestFullVerticle extends AbstractVerticle {

    private static final String COLLECTION = "collection";
    private static final String ACTION = "action";

    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);
        router.route().consumes("application/json");
        router.route().produces("application/json");
        router.route().handler(BodyHandler.create());
        router.get("/api/:collection").handler(this::findAllHandler);
        router.get("/api/:collection/:id").handler(this::findByIdHandler);
        router.post("/api/:collection").handler(this::createHandler);
        router.put("/api/:collection").handler(this::updateHandler);
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

    private void findByIdHandler(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions().addHeader(ACTION, "findById");
        JsonObject object = routingContext.getBodyAsJson();
        object.put(COLLECTION, routingContext.request().getParam(COLLECTION));
        object.put("id", routingContext.request().getParam("id"));
        reply(routingContext, options, object);
    }

    private void findAllHandler(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions().addHeader(ACTION, "findAll");
        JsonObject object = new JsonObject().put(COLLECTION, routingContext.request().getParam(COLLECTION));
        reply(routingContext, options, object);
    }

    private void createHandler(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions().addHeader(ACTION, "create");
        JsonObject object = routingContext.getBodyAsJson();
        object.put(COLLECTION, routingContext.request().getParam(COLLECTION));
        reply(routingContext, options, object);
    }

    private void updateHandler(RoutingContext routingContext) {
        DeliveryOptions options = new DeliveryOptions().addHeader(ACTION, "update");
        JsonObject object = routingContext.getBodyAsJson();
        object.put(COLLECTION, routingContext.request().getParam(COLLECTION));
        reply(routingContext, options, object);
    }

    private void reply(RoutingContext routingContext, DeliveryOptions options, JsonObject object) {
        vertx.eventBus().<String>send(ServicesVerticle.class.getName(), object, options, messageAsyncResult -> {
            if (messageAsyncResult.succeeded()) {
                routingContext.response().end(messageAsyncResult.result().body());
            } else {
                routingContext.response().end(messageAsyncResult.cause().getMessage());
            }
        });
    }
}
