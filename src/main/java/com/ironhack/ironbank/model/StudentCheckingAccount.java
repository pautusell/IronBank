package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.utils.Money;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("student_checking_account")
public class StudentCheckingAccount extends Account{
    private String secretKey = String.valueOf(UUID.randomUUID());

    public StudentCheckingAccount() {
        super.setAccountType(AccountType.STUDENT_CHECKING);
    }
    public StudentCheckingAccount(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
        super.setAccountType(AccountType.STUDENT_CHECKING);
    }
}
