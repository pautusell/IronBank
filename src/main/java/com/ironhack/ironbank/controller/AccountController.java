package com.ironhack.ironbank.controller;

import com.ironhack.ironbank.dto.*;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.service.AccountService;
import com.ironhack.ironbank.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @GetMapping("/list")
    public List<AccountDto> getMyAccounts(){
        return userService.getAccountsByUserLogged();
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto newAccount(@RequestBody @Valid AccountDto accountDto){
        return accountService.createAccount(accountDto);
    }

    @PatchMapping("/{id}/secondary-owner")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto editSecondaryOwner(@PathVariable(name = "id") Long accountId, @RequestParam String secondaryAccountHolderUsername){
        return accountService.editSecondaryOwner(accountId, secondaryAccountHolderUsername);
    }

    @PatchMapping("/{id}/edit-balance")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto editBalance(@PathVariable(name = "id") Long accountId, @RequestParam BigDecimal newBalance){
        return accountService.editBalance(accountId, newBalance);
    }

    @PatchMapping("/{id}/conditions")
    public AccountFullDto editAccountConditions(@PathVariable(name = "id") Long accountId,
                                               @RequestParam Optional<BigDecimal> penaltyFee,
                                               @RequestParam @DecimalMin("100") Optional<BigDecimal> minimumBalance,
                                               @RequestParam Optional<BigDecimal> monthlyMaintenanceFee,
                                               @RequestParam @DecimalMax("0.5") Optional<BigDecimal> interestRate,
                                               @RequestParam Optional<BigDecimal> creditLimit,
                                               @RequestParam Optional<Status> status
                                                ){
        return accountService.editAccountConditions(accountId, penaltyFee, minimumBalance, monthlyMaintenanceFee,
                interestRate, creditLimit, status);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteAccount(@PathVariable(name = "id") Long accountId){
        return accountService.deleteAccount(accountId);
    }

}
