package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.model.AccountHolder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AccountHolderAddressDtoResponse {

    private Long id;
    private String name;
    private String username;
    private LocalDate dob;
    private String email;
    private String mainAddress;
    private String mailingAddress;

    public static AccountHolderAddressDtoResponse fromAccountHolder(AccountHolder accountHolder){
        var accHolderDto = new AccountHolderAddressDtoResponse();
        accHolderDto.setId(accountHolder.getId());
        accHolderDto.setName(accountHolder.getName());
        accHolderDto.setUsername(accountHolder.getUsername());
        accHolderDto.setDob(accountHolder.getDob());
        accHolderDto.setEmail(accountHolder.getEmail());
        if (accountHolder.getAddress() != null) {
            accHolderDto.setMainAddress(accountHolder.getAddress().getStreetName() + ", "
                    + accountHolder.getAddress().getPostalCode() + ", "
                    + accountHolder.getAddress().getCity() + ", "
                    + accountHolder.getAddress().getProvince());
        }
        if (accountHolder.getMailingAddress() != null) {
            accHolderDto.setMailingAddress(accountHolder.getMailingAddress().getStreetName() + ", "
                    + accountHolder.getMailingAddress().getPostalCode() + ", "
                    + accountHolder.getMailingAddress().getCity() + ", "
                    + accountHolder.getMailingAddress().getProvince());
        }
        return accHolderDto;
    }
}
