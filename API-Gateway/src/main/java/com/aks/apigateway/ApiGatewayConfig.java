package com.aks.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/users/**")
							.uri("lb://users-service")
						)
				.route(r -> r.path("/books/**")
						.uri("lb://books-service")
					)
				.route(r -> r.path("/books-new/**")
						.filters(f -> f.rewritePath("/books-new(?<segment>.*)",
								"/books${segment}"))
						.uri("lb://books-service")
					)
				.build();
	}

}
