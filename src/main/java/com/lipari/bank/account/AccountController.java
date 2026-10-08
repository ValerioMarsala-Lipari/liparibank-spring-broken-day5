package com.lipari.bank.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Gestione conti bancari")
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @GetMapping
    @Operation(summary = "Lista tutti i conti")
    public ResponseEntity<List<Account>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dettaglio conto")
    public ResponseEntity<Account> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina conto (solo ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seed")
    @Operation(summary = "Crea conti di test")
    public ResponseEntity<List<Account>> seed() {
        Account mario = new Account("IT60X0542811101000000123456", "Mario Rossi",
                new BigDecimal("5000.00"));
        Account luigi = new Account("IT60X0542811101000000654321", "Luigi Verdi",
                new BigDecimal("1000.00"));
        return ResponseEntity.ok(accountRepository.saveAll(List.of(mario, luigi)));
    }
}
