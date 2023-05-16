package ua.kpi.ecollab.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.kpi.ecollab.gateway.exception.JwtTokenMalformedException;
import ua.kpi.ecollab.gateway.exception.JwtTokenMissingException;
import ua.kpi.ecollab.gateway.util.Util;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthenticationFilter implements GatewayFilter {

    private final Util util;

    @Autowired
    public AuthenticationFilter(Util util) {
        this.util = util;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authEndpoints = List.of("/api/auth/login");
        Predicate<ServerHttpRequest> isApiSecured = r -> authEndpoints.stream()
                .noneMatch(uri -> r.getURI().getPath().contains(uri));

        if (isApiSecured.test(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            String token = request.getHeaders().getOrEmpty("Authorization").get(0);
            try {
                util.validateToken(token);
            } catch (JwtTokenMalformedException | JwtTokenMissingException e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }
        }
        return chain.filter(exchange);
    }

}
