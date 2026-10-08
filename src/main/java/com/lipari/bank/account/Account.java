package com.lipari.bank.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 34)
    private String iban;

    @Column(nullable = false, length = 200)
    private String intestatario;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    public Account(String iban, String intestatario, BigDecimal balance) {
        this.iban = iban;
        this.intestatario = intestatario;
        this.balance = balance;
    }
}
