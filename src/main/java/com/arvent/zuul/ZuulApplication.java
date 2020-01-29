package com.arvent.zuul;

import com.arvent.zuul.provider.GenericZuulFallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableSwagger2
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}

	@Bean
	public FallbackProvider routeZuulFallbackProvider() {
		GenericZuulFallbackProvider routeZuulFallback = new GenericZuulFallbackProvider();
		routeZuulFallback.setRoute("*");
//		routeZuulFallback.setRoute("route2");
		routeZuulFallback.setRawStatusCode(200);
		routeZuulFallback.setStatusCode(HttpStatus.OK);
//		routeZuulFallback.setResponseBody("We are little busy. Comeback After Sometime");
		return routeZuulFallback;
	}
}

