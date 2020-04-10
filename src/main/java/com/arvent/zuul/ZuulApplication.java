package com.arvent.zuul;


import com.arvent.zuul.provider.GenericZuulFallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;


@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableCircuitBreaker
//@EnableHystrixDashboard //If all your hystrix commands are from zuul, there are no thread pools as it uses semaphore isolation.
//@RibbonClient(name="RandomRule",configuration = RibbonConfig.class)
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}

	//@Bean
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

