package com.lipari.bank.shared.config;

import com.lipari.bank.auth.BankUserRepository;
import com.lipari.bank.auth.model.BankUser;
import com.lipari.bank.auth.model.Role;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        if (userRepository.count() > 0) {
            return;
        }

        Role adminRole = new Role("ROLE_ADMIN");
        Role customerRole = new Role("ROLE_CUSTOMER");

        entityManager.persist(adminRole);
        entityManager.persist(customerRole);

        BankUser admin = new BankUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(adminRole));

        BankUser customer = new BankUser();
        customer.setUsername("customer");
        customer.setPassword(passwordEncoder.encode("customer123"));
        customer.setEnabled(true);
        customer.setRoles(Set.of(customerRole));

        userRepository.save(admin);
        userRepository.save(customer);

        log.info("Utenti di test inizializzati: admin, customer");
    }
}