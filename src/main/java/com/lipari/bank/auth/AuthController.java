package com.lipari.bank.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Autenticazione LipariBank")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @Operation(summary = "Login utente", description = "Autentica un utente con username e password")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        log.info("Login effettuato: {} con authorities: {}", authentication.getName(), authorities);

        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authorities,
                "message", "Login effettuato. Al Giorno 6 qui troverai il token JWT."
        ));
    }

    public record LoginRequest(String username, String password) {}
}
