package com.lipari.bank.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conto non trovato: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        log.warn("Eliminazione conto {} richiesta da: {}",
                id, SecurityContextHolder.getContext().getAuthentication().getName());
        accountRepository.deleteById(id);
    }
}
