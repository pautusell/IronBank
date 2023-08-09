package com.ironhack.ironbank.controller;

import com.ironhack.ironbank.dto.*;
import com.ironhack.ironbank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
public class OperationController {

    private final AccountService accountService;

    @GetMapping("/account/{id}/balance")
    public CheckBalanceResponse checkBalance(@PathVariable("id") Long accountId){
        return accountService.checkBalance(accountId);
    }

    @GetMapping("/account/{id}/statement")
    public AccountStatement getAccountStatement(@PathVariable("id") Long accountId){
        return accountService.getAccountStatement(accountId);
    }

    @PutMapping("/account/{id}/withdrawal")
    public WithdrawalResponse withdrawal(@PathVariable("id") Long accountId, @RequestParam BigDecimal amount) {
        return accountService.withdrawal(accountId, amount);
    }

    @PutMapping("/account/{id}/deposit")
    public DepositResponse deposit(@PathVariable("id") Long accountId, @RequestParam BigDecimal amount) {
        return accountService.deposit(accountId, amount);
    }

    @PutMapping("/account/{origin_id}/transfer/{destination_id}")
    public TransferResponse deposit(@PathVariable("origin_id") Long originAccountId, @RequestParam BigDecimal amount,
                                    @PathVariable("destination_id") Long destinationAccountId, @RequestParam String description) {
        return accountService.transfer(originAccountId, amount, destinationAccountId, description);
    }

    @PutMapping("/account/{id}/credit-card-withdrawal")
    public CreditWithdrawalResponse creditCardWithdrawal(@PathVariable("id") Long accountId,
                                                     @RequestParam BigDecimal amount) {
        return accountService.creditCardWithdrawal(accountId, amount);
    }

    @PutMapping("/card/{id}/credit-card-purchase")
    public CreditPurchaseResponse creditCardPurchase(@PathVariable("id") Long accountId,
                                                     @RequestParam String cardSecretKey,
                                                     @RequestParam BigDecimal amount,
                                                     @RequestParam String merchantDescription) {
        return accountService.creditCardPurchase(accountId, cardSecretKey, amount, merchantDescription);
    }

}
