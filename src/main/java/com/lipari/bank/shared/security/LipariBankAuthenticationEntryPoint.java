package com.lipari.bank.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class LipariBankAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Accesso non autenticato (401) — risorsa: {} {} — motivo: {}",
                request.getMethod(),
                request.getRequestURI(),
                authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorBody = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 401,
                "error", "Unauthorized",
                "message", "Autenticazione richiesta. Fornire le credenziali valide.",
                "path", request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), errorBody);
    }
}
