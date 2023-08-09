package com.ironhack.ironbank.controller;

import com.ironhack.ironbank.dto.AccountDto;
import com.ironhack.ironbank.dto.TransactionDto;
import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.service.AccountService;
import com.ironhack.ironbank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/activity/{id}/transactions")
    public List<TransactionDto> getAccountTransactionsByPeriod(@PathVariable("id") Long accountId, LocalDate startDate, LocalDate endDate){
        return transactionService.getAccountTransactionsByPeriod(accountId, startDate, endDate);
    }

    @GetMapping("/admin/accounts-overview/{type}")
    public List<AccountDto> getOverviewOfAccountsByType(@PathVariable("type") AccountType accountType){
        return accountService.getOverviewOfAccountsByType(accountType);
    }

    // TODO: Add query for Count of Accounts By Type per User + Users by Year of DOB + Users by Avg Balance on Savings


}
