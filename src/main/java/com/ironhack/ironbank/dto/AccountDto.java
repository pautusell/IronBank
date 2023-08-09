package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.model.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {
    private Long id;
    private BigDecimal balance = BigDecimal.ZERO;
    @NotNull
    private AccountType accountType;
    @NotBlank
    private String primaryOwnerUsername;
    private String secondaryOwnerUsername;
    private Status status;

    public static AccountDto fromAccount(Account account){
        var accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setBalance(account.getBalance().getAmount());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setPrimaryOwnerUsername(account.getPrimaryOwner().getUsername());
        if(account.getSecondaryOwner() != null) accountDto.setSecondaryOwnerUsername(account.getSecondaryOwner().getUsername());
        accountDto.setStatus(account.getStatus());
        return accountDto;
    }

}
