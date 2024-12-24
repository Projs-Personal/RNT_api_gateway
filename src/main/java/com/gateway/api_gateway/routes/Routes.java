package com.gateway.api_gateway.routes;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.*;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute()
    {
        //THIS IS a different PROGRAMMING MODEL :
        //FUNCTIONAL ENDPOINT PROGRAMMING MODEL : webmvc.fn -> a light.wt functional prog model in which funcs are used to route and handle requests & contracts
        //are designed for immutabbility. it is an alt to annotation-based model, but other wise runs on the same dispatcher servlet
        return route("product_service")
                .route(RequestPredicates.path("/api/product"), HandlerFunctions.http("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))//define a circuit breaker as a filter & add circuit breaker filter fun which takes ID and fallback url(already fallback route defined at bottom)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute()
    {
        return route("order_service")
                .route(RequestPredicates.path("/api/order"), HandlerFunctions.http("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))//define a circuit breaker as a filter & add circuit breaker filter fun which takes ID and fallback url(already fallback route defined at bottom)

                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute()
    {
        return route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"), HandlerFunctions.http("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))//define a circuit breaker as a filter & add circuit breaker filter fun which takes ID and fallback url(already fallback route defined at bottom)

                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                //whenever req is received, call request lambda -> send this statement : and return the response
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service Unavailable, try again later!"))
                .build();
    }
}
