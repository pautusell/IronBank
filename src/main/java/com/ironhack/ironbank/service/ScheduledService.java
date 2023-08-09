package com.ironhack.ironbank.service;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.model.*;
import com.ironhack.ironbank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private static final int INTEREST_PERIOD_SAVINGS = 365;
    private static final int INTEREST_PERIOD_CREDIT_CARD = 30;
    private static final int MAINTENANCE_FEE_PERIOD = 30;

    @Scheduled(cron = "0 15 3 * * ?") // Every day at 2:30 am
    public void applyMaintenanceFee() {
        List<Long> listOfCheckingAccountIds = accountRepository.findAllIdsByAccountType(AccountType.CHECKING.name());
        var updateTime = LocalDateTime.now();
        for (Long accountId : listOfCheckingAccountIds) {
            var account = accountRepository.findById(accountId).orElseThrow();
            var lastTime = ((CheckingAccount) account).getLastTimeMaintenanceFeeApplied();
            var maintenanceFee = ((CheckingAccount) account).getMonthlyMaintenanceFee();
            if (Duration.between(lastTime, updateTime).compareTo(Duration.ofDays(MAINTENANCE_FEE_PERIOD)) > 0) {
                account.getBalance().decreaseAmount(maintenanceFee);
                ((CheckingAccount) account).setLastTimeMaintenanceFeeApplied(updateTime);
                var transaction = new Transaction(TransactionType.ACCOUNT_FEE, account, maintenanceFee.negate());
                transaction.setDescription("Monthly Maintenance Fee");
                transactionService.save(transaction);
                accountRepository.save(account);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1am
    public void applySavingsInterest() {
        List<Long> listOfSavingsAccountIds = accountRepository.findAllIdsByAccountType(AccountType.SAVINGS.name());
        var updateTime = LocalDateTime.now();
        for (Long accountId : listOfSavingsAccountIds) {
            var account = accountRepository.findById(accountId).orElseThrow();
            var lastTime = ((SavingsAccount) account).getLastTimeInterestApplied();
            if (Duration.between(lastTime, updateTime).compareTo(Duration.ofDays(INTEREST_PERIOD_SAVINGS)) > 0) {
                var avgBalanceForInterestCalculation = transactionService.averageBalanceInPeriod(accountId, lastTime, updateTime);
                var interestAmount = ((SavingsAccount) account).getInterestRate().multiply(avgBalanceForInterestCalculation);
                account.getBalance().increaseAmount(interestAmount);
                ((SavingsAccount) account).setLastTimeInterestApplied(updateTime);
                var transaction = new Transaction(TransactionType.ACCOUNT_INTEREST, account, interestAmount);
                transaction.setDescription("Interest returns on your Savings balance");
                transactionService.save(transaction);
                accountRepository.save(account);
            }
        }
    }

    @Scheduled(cron = "0 15 3 * * ?") // Every day at 2am
    public void applyCreditInterest(){
        List<Long> listOfCreditCardAccountsId = accountRepository.findAllIdsByAccountType(AccountType.CREDIT_CARD.name());
        var updateTime = LocalDateTime.now();
        for (Long accountId : listOfCreditCardAccountsId){
            var account = accountRepository.findById(accountId).orElseThrow();
            var lastTime = ((CreditCardAccount)account).getLastTimeInterestApplied();
            if (Duration.between(lastTime, updateTime).compareTo(Duration.ofDays(INTEREST_PERIOD_CREDIT_CARD)) > 0){
                var avgBalanceForInterestCalculation = transactionService.averageBalanceInPeriod(accountId, lastTime, updateTime);
                var interestAmount = ((CreditCardAccount)account).getInterestRate().multiply(avgBalanceForInterestCalculation);
                account.getBalance().increaseAmount(interestAmount);
                ((CreditCardAccount)account).setLastTimeInterestApplied(updateTime);
                var transaction = new Transaction(TransactionType.ACCOUNT_INTEREST, account, interestAmount);
                transaction.setDescription("Interest on your Credit balance");
                transactionService.save(transaction);
                accountRepository.save(account);
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * ?") // Every day at 3am
    public void updateHistoricMaxAmounts(){
        var updateTime = LocalDateTime.now();
        var scopeOfAccountTypes = List.of(
                AccountType.CHECKING,
                AccountType.STUDENT_CHECKING);
        for (AccountType accountType : scopeOfAccountTypes) {
            List<Account> accountBatch = accountRepository.findAllByAccountType(accountType.name());
            for (Account account : accountBatch) {
                var dailySpent = transactionService.sumCashOutflowsInPeriod(account.getId(), updateTime.minusDays(1), updateTime);
                if (dailySpent.compareTo(account.getHistoricMaxDailySpent()) > 0) {
                    account.setHistoricMaxDailySpent(dailySpent);
                    accountRepository.save(account);
                }
            }
        }
        List<Account> creditCardAccounts = accountRepository.findAllByAccountType(AccountType.CREDIT_CARD.name());
        for (Account account : creditCardAccounts) {
            var dailySpent = transactionService.sumCreditAmountsInPeriod(account.getId(), updateTime.minusDays(1), updateTime);
            if (dailySpent.compareTo(account.getHistoricMaxDailySpent()) > 0) {
                account.setHistoricMaxDailySpent(dailySpent);
                accountRepository.save(account);
            }
        }
    }


}
