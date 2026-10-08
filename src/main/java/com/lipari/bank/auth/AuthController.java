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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lipari.bank.auth.model.BankUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Autenticazione LipariBank")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/change-password")
    @Operation(summary = "Cambio password", description = "Permette all'utente autenticato di cambiare la propria password")
    public ResponseEntity<Map<String, String>> changePassword(
        @RequestBody ChangePasswordRequest request) {

        String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

        BankUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "La password attuale non è corretta"));
        }

        if (request.newPassword() == null || request.newPassword().length() < 8) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "La nuova password deve contenere almeno 8 caratteri"));
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(
            Map.of("message", "Password modificata con successo")
        );
    }

    public record LoginRequest(String username, String password) {}
    public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
    ) {}
}
