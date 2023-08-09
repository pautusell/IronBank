package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.utils.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("checking_account")
public class CheckingAccount extends Account{
    private String secretKey = String.valueOf(UUID.randomUUID());
    @DecimalMin(value = "250")
    private BigDecimal minimumBalance = BigDecimal.valueOf(250);
    private BigDecimal monthlyMaintenanceFee = BigDecimal.valueOf(12);
    private LocalDateTime lastTimeMaintenanceFeeApplied = LocalDateTime.now();

    public CheckingAccount() {
        super.setAccountType(AccountType.CHECKING);
    }

    public CheckingAccount(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
        super.setAccountType(AccountType.CHECKING);
    }

}
