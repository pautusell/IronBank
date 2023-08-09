package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.utils.Money;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountFullDto {

    private Long id;
    private Money balance;
    private AccountType accountType;
    private BigDecimal penaltyFee;
    private Long secretKey;
    private BigDecimal minimumBalance;
    private BigDecimal monthlyMaintenanceFee;
    private Status status;
    private BigDecimal interestRate;
    private BigDecimal creditLimit;

}
