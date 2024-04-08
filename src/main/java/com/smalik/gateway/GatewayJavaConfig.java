package com.smalik.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Profile("java")
@Configuration
public class GatewayJavaConfig {

    private final Logger logger = LoggerFactory.getLogger(GatewayJavaConfig.class);

    @Bean
    public RouterFunction<ServerResponse> heroesRoute () {
        return route()
                .GET("/heroes/**", http("http://localhost:8080"))
                .POST("/heroes/**", http("http://localhost:8080"))
                .after((req, resp) -> {
                    logger.info(String.format("Response: Uri=%s, Method=%s, Status=%d, Headers=%s", req.path(), req.method(), resp.statusCode().value(), resp.headers()));
                    return resp;
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> httpbinRoute () {
        return route()
                .GET("/httpbin/**", http("https://httpbin.org"))
                .POST("/httpbin/**", http("https://httpbin.org"))
                .DELETE("/httpbin/**", http("https://httpbin.org"))
                .before(stripPrefix(1))
                .before(addRequestHeader("header-1", "value-1"))
                .before(addRequestHeader("header-2", "value-2"))
                .before(request -> ServerRequest
                        .from(request)
                        .header("header-3", "value-3")
                        .header("header-4", "value-4")
                        .build())
                .build();
    }
}
