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
@DiscriminatorValue("savings_account")
public class SavingsAccount extends Account{
    private String secretKey = String.valueOf(UUID.randomUUID());
    @DecimalMin(value = "100")
    private BigDecimal minimumBalance = BigDecimal.valueOf(1000);
    @DecimalMax(value = "0.5")
    @Column(precision = 5, scale = 4)
    private BigDecimal interestRate = BigDecimal.valueOf(0.0025);
    private LocalDateTime lastTimeInterestApplied = LocalDateTime.now();

    public SavingsAccount() {
        super.setAccountType(AccountType.SAVINGS);
    }

    public SavingsAccount(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
        super.setAccountType(AccountType.SAVINGS);
    }
}
