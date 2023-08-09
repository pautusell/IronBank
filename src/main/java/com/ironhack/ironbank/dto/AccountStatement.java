package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AccountStatement {

    private Long accountId;
    private AccountType typeOfAccount;
    private Status status;
    private String primaryOwnerName;
    private String secondaryOwnerName;
    private BigDecimal balance;
    private List<TransactionDto> lastTransactions;

}
