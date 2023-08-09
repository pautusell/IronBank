package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.utils.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("credit_card_account")
public class CreditCardAccount extends Account{

    private String cardSecretKey = String.valueOf(UUID.randomUUID());
    @DecimalMax(value = "100000")
    private BigDecimal creditLimit = BigDecimal.valueOf(100);
    @DecimalMin(value = "0.1")
    @Column(precision = 5, scale = 4)
    private BigDecimal interestRate = BigDecimal.valueOf(0.2);
    private LocalDateTime lastTimeInterestApplied = LocalDateTime.now();

    public CreditCardAccount() {
        super.setAccountType(AccountType.CREDIT_CARD);
    }
    public CreditCardAccount(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
        super.setAccountType(AccountType.CREDIT_CARD);
    }
}
