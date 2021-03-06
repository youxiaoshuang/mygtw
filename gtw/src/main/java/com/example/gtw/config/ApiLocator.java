package com.example.gtw.config;

import com.example.gtw.filter.RequestBodyReadFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * @Description: read request body
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@Configuration
@EnableAutoConfiguration
@Log4j2
public class ApiLocator {

  @Autowired
  RequestBodyReadFilter requestBodyReadFilter;

  String apilocatorService = "/account/index/**";
  String apilocatorUri = "http://localhost:8091";

  /**
   * route1 是get请求，get请求使用readBody会报错 route2 是post请求，Content-Type是application/x-www-form-urlencoded，readbody为String.class
   * route3 是post请求，Content-Type是application/json,readbody为Object.class
   */
  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder) {

    Builder routes = builder.routes();
    routes.route("route1",
        r -> r
            .method(HttpMethod.GET)
            .and()
            .path(apilocatorService)
            .filters(f -> {
              f.filter(requestBodyReadFilter);
              return f;
            })
            .uri(apilocatorUri))
        .route("route2",
            r -> r
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .and()
                .method(HttpMethod.POST)
                .and()
                .readBody(String.class, readBody -> {
                  log.info("request method POST, Content-Type is application/x-www-form-urlencoded, body  is:{}",
                      readBody);
                  return true;
                })
                .and()
                .path(apilocatorService)
                .filters(f -> {
                  f.filter(requestBodyReadFilter);
                  return f;
                })
                .uri(apilocatorUri))
        .route("route3",
            r -> r
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .and()
                .method(HttpMethod.POST)
                .and()
                .readBody(Object.class, readBody -> {
                  log.info("request method POST, Content-Type is application/json, body  is:{}", readBody);
                  return true;
                })
                .and()
                .path(apilocatorService)
                .filters(f -> {
                  f.filter(requestBodyReadFilter);
                  return f;
                })
                .uri(apilocatorUri));
    RouteLocator routeLocator = routes.build();
    log.info("custom RouteLocator is loading ... {}", routeLocator);
    return routeLocator;
  }


}

