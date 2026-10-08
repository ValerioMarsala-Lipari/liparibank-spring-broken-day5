package com.lipari.bank.auth;

import com.lipari.bank.auth.model.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankUserRepository extends JpaRepository<BankUser, Long> {

    Optional<BankUser> findByUsername(String username);
}
