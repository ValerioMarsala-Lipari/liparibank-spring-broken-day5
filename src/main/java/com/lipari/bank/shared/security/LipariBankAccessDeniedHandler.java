package com.lipari.bank.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class LipariBankAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("Accesso negato (403) — utente: {} — risorsa: {} {}",
                request.getUserPrincipal() != null
                        ? request.getUserPrincipal().getName() : "unknown",
                request.getMethod(),
                request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorBody = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 403,
                "error", "Forbidden",
                "message", "Non hai i permessi necessari per accedere a questa risorsa",
                "path", request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), errorBody);
    }
}
