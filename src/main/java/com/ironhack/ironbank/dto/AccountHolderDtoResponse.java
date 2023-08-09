package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.model.AccountHolder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AccountHolderDtoResponse {

    private Long id;
    private String name;
    private String username;
    private LocalDate dob;
    private String email;

    public static AccountHolderDtoResponse fromAccountHolder(AccountHolder accountHolder){
        var accHolderDto = new AccountHolderDtoResponse();
        accHolderDto.setId(accountHolder.getId());
        accHolderDto.setName(accountHolder.getName());
        accHolderDto.setUsername(accountHolder.getUsername());
        accHolderDto.setDob(accountHolder.getDob());
        accHolderDto.setEmail(accountHolder.getEmail());
        return accHolderDto;
    }

}
