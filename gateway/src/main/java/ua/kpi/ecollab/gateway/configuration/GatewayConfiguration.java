package ua.kpi.ecollab.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.kpi.ecollab.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfiguration {

  private final AuthenticationFilter filter;

  @Autowired
  public GatewayConfiguration(AuthenticationFilter filter) {
    this.filter = filter;
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
    return routeLocatorBuilder
        .routes()
        .route(
            "user-service",
            r ->
                r.path("/api/searches/**")
                    .filters(f -> f.filter(filter))
                    .uri("http://localhost:8081"))
        .route(
            "recommendations",
            r ->
                r.path("/api/recommendations/**")
                    .filters(f -> f.filter(filter))
                    .uri("http://localhost:8082"))
        .route(
            "query-maker",
            r ->
                r.path("/api/query-maker/**")
                    .filters(f -> f.filter(filter))
                    .uri("http://localhost:8083"))
        .route(
            "auth",
            r ->
                r.path("/api/auth/**")
                    .filters(f -> f.filter(filter))
                    .uri("http://localhost:8085"))
        .build();
  }
}
