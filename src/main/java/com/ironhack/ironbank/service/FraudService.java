package com.ironhack.ironbank.service;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.model.Account;
import com.ironhack.ironbank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudService {

    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    public Account fraudValidations(Account account, BigDecimal amount) {
        account = fraudDailySpentValidation(account, amount);
        account = fraudTransactionAmountValidation(account, amount);
        account = fraudConsecutiveTransactionsValidations(account);
        return account;
    }

    // 1: If spent in last 24h (including current transaction) is greater than 150% of the historical daily max --> FREEZE ACCOUNT
    public Account fraudDailySpentValidation(Account account, BigDecimal amount) {

        var currentTime = LocalDateTime.now();
        BigDecimal dailySpent;
        if (!account.getAccountType().equals(AccountType.CREDIT_CARD)) {
            dailySpent = transactionService.sumCashOutflowsInPeriod(account.getId(), currentTime.minusDays(1), currentTime).add(amount);
        } else { dailySpent = transactionService.sumCreditAmountsInPeriod(account.getId(), currentTime.minusDays(1), currentTime).add(amount);}
        if (dailySpent.compareTo(account.getHistoricMaxDailySpent().multiply(BigDecimal.valueOf(1.5))) > 0) {
            account.setStatus(Status.FROZEN);
            accountRepository.save(account);
        }
        return account;
    }

    // 2: If current transaction amount is greater than 150% of last 3 months average transaction amount --> ASK CONFIRMATION
    public Account fraudTransactionAmountValidation(Account account, BigDecimal amount) {
        var currentTime = LocalDateTime.now();
        if (!account.getAccountType().equals(AccountType.CREDIT_CARD)) {
            var avgAmount = transactionService.avgCashOutflowsInPeriod(account.getId(), currentTime.minusDays(90), currentTime);
            if (amount.compareTo(avgAmount.multiply(BigDecimal.valueOf(1.5))) > 0) {
                // [NOT IMPLEMENTED]--> request confirmation of operation by online banking mobile app (MFA)
            }
        }
        return account;
    }

    // 3: If previous transaction (and then 3 transactions before) was less than 1 second ago --> FREEZE ACCOUNT
    public Account fraudConsecutiveTransactionsValidations(Account account) {

        var currentTime = LocalDateTime.now();
        var last3Transactions = transactionService.findLastThreeUserTransactions(account.getId());
        if (last3Transactions.size() == 3) {
            if (Duration.between(last3Transactions.get(2).getTimestamp(), currentTime).compareTo(Duration.ofSeconds(1)) < 0) {
                account.setStatus(Status.FROZEN);
                accountRepository.save(account);
            }
        }
        return account;
    }
}
