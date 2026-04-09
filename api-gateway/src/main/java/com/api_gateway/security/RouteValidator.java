package com.api_gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    private static final List<String> openApiEndpoints = List.of(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/oauth2/*",
            "/login/oauth2/*"
    );

    public Predicate<String> isSecured =
            uri -> openApiEndpoints.stream()
                    .noneMatch(uri::startsWith);

}
