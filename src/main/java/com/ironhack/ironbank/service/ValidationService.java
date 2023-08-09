package com.ironhack.ironbank.service;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.exception.OperationalException;
import com.ironhack.ironbank.exception.UnauthorizedException;
import com.ironhack.ironbank.exception.UnregisteredTPException;
import com.ironhack.ironbank.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserService userService;
    private final TransactionService transactionService;
    private static final int PENALTY_FEE_GRACE_PERIOD = 2;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(authentication.getName());
    }

    public Account checkOwnerOrAdmin(Account account) {
        var checkOK = false;
        if(getCurrentUser().getRoles().contains("ADMIN")) checkOK = true;
        else if (account.getPrimaryOwner().getUsername().equals(getCurrentUser().getUsername())) checkOK = true;
        else if (account.getSecondaryOwner() != null) {
                if (account.getSecondaryOwner().getUsername().equals(getCurrentUser().getUsername())) checkOK = true;
        }
        if(checkOK) return account;
        else throw new UnauthorizedException(account.getId());
    }

    public Account checkBalanceConditions(Account account) {
        var accountType= account.getAccountType();
        if (accountType.equals(AccountType.CHECKING) || accountType.equals(AccountType.SAVINGS)) {
            BigDecimal minimumBalance =
            switch (accountType) {
                case CHECKING -> ((CheckingAccount) account).getMinimumBalance();
                case SAVINGS -> ((SavingsAccount) account).getMinimumBalance();
                default -> throw new IllegalStateException("Unexpected value: " + accountType);
            };
            if (account.getBalance().getAmount().compareTo(minimumBalance) <= 0) {
                boolean applyOK = false;
                var lastPenalty = transactionService.findLastTransactionByType(account.getId(), TransactionType.ACCOUNT_PENALTY);
                if (lastPenalty.isPresent()) {
                    var timeStampOfLast = lastPenalty.get().getTimestamp();
                    if (Duration.between(timeStampOfLast, LocalDateTime.now()).compareTo(Duration.ofDays(PENALTY_FEE_GRACE_PERIOD)) > 0) {
                        applyOK = true;
                    }
                } else {
                    applyOK = true;
                }
                if (applyOK) {
                    account = applyPenaltyFee(account);
                }
            }
        }
        return account;
    }

    public Account applyPenaltyFee(Account account){
        account.getBalance().decreaseAmount(account.getPenaltyFee());
        var transaction = new Transaction(TransactionType.ACCOUNT_PENALTY, account, account.getPenaltyFee().negate());
        transaction.setDescription("Penalty fee: balance below minimum for this account");
        transactionService.save(transaction);
        return account;
    }

    public Account checkOwnerAndConditions(Account account){
        account = checkOwnerOrAdmin(account);
        account = checkBalanceConditions(account);
        //account = checkInterestsAndFees(account);
        return account;
    }

    public Account checkAccountActive (Account account){
        if(account.getStatus().equals(Status.FROZEN)) throw new OperationalException("The account " + account.getId() + " is frozen");
        else return account;
    }

    public Account checkEnoughBalance (Account account, BigDecimal amount){
        if(account.getBalance().getAmount().compareTo(amount) >= 0) return account;
        else throw new OperationalException("The account " + account.getId() + " does not have enough balance to perform this operation");
    }

    public Account checkSecretKey(Account account, String secretKey){
        var checkOK = false;
        switch (account.getAccountType()){
            case CHECKING -> {
                if(((CheckingAccount)account).getSecretKey().equals(secretKey)) checkOK = true;
            }
            case STUDENT_CHECKING -> {
                if(((StudentCheckingAccount)account).getSecretKey().equals(secretKey)) checkOK = true;
            }
            case CREDIT_CARD -> {
                if(((CreditCardAccount)account).getCardSecretKey().equals(secretKey)) checkOK = true;
            }
        }
        if (checkOK){
            return account;
        } else throw new UnauthorizedException(account.getId());
    }

    public Account checkCreditLimit(Account account, BigDecimal amount) {
        if(((CreditCardAccount)account).getCreditLimit().compareTo(account.getBalance().getAmount().add(amount)) >= 0) return account;
        else throw new OperationalException("The credit card " + account.getId() + " does not have enough credit limit left to perform this operation");
    }

    public void checkRegisteredTP(String hashedKey) {
        if(hashedKey.isBlank()) throw new UnregisteredTPException(hashedKey);
//        // If registry of Third Parties wants to be implemented:
//        var foundTP = TpRepository.findByHashedKey(hashedKey);
//        if (foundTP.isEmpty) throw new UnregisteredTPException(hashedKey);
    }




   /*   -----> OLD CODE BEFORE SCHEDULER -------------------------------------------

   public Account checkInterestsAndFees(Account account) {
        switch (account.getAccountType()){
            case CHECKING -> {
                var lastMaintenanceFee = ((CheckingAccount)account).getLastTimeMaintenanceFeeApplied();
                if (needToReapply(lastMaintenanceFee, MAINTENANCE_FEE_PERIOD)) {
                    account = applyMaintenanceFee(account);
                }
            }
            case SAVINGS -> {
                var lastInterests = ((SavingsAccount)account).getLastTimeInterestApplied();
                if (needToReapply(lastInterests, INTEREST_PERIOD_SAVINGS)) {
                    account = applyInterests(account);
                }
            }
            case CREDIT_CARD -> {
                var lastInterests = ((CreditCardAccount)account).getLastTimeInterestApplied();
                if (needToReapply(lastInterests, INTEREST_PERIOD_CREDIT_CARD)) {
                    account = applyInterests(account);
                }
            }
        }
        return account;
    }

    public boolean needToReapply(LocalDateTime lastTimeApplied, int definedPeriod){
        var updateNeeded = false;
        if (lastTimeApplied != null) {
            if (Duration.between(lastTimeApplied, LocalDateTime.now())
                    .compareTo(Duration.ofDays(definedPeriod)) > 0) {
                updateNeeded = true;
            }
        } else updateNeeded = true;
        return updateNeeded;
    }


    public Account applyInterests(Account account) {
        var balanceForInterestCalculation = account.getBalance().getAmount();
        // CALCULATION OF AVG Balance (above) should take into account the period of un-applied interests. If it has been longer (e.g. 3 years) it should be applied
        // 3 times with the according amounts.

        switch (account.getAccountType()){
            case SAVINGS -> {
                var interestAmount = ((SavingsAccount)account).getInterestRate().multiply(balanceForInterestCalculation);
                account.getBalance().increaseAmount(interestAmount);
                ((SavingsAccount)account).setLastTimeInterestApplied(LocalDateTime.now());
                var transaction = new Transaction(TransactionType.ACCOUNT_INTEREST, account, interestAmount);
                transaction.setDescription("Interest returns on your Savings balance");
                transactionService.save(transaction);
            }
            case CREDIT_CARD -> {
                var interestAmount = ((CreditCardAccount)account).getInterestRate().multiply(balanceForInterestCalculation);
                account.getBalance().decreaseAmount(interestAmount);
                ((CreditCardAccount)account).setLastTimeInterestApplied(LocalDateTime.now());
                var transaction = new Transaction(TransactionType.ACCOUNT_INTEREST, account, interestAmount.negate());
                transaction.setDescription("Interest on your Credit amounts");
                transactionService.save(transaction);
            }
        }
        return account;
    }

    public Account applyMaintenanceFee(Account account) {
        account.getBalance().decreaseAmount(((CheckingAccount)account).getMonthlyMaintenanceFee());
        ((CheckingAccount)account).setLastTimeMaintenanceFeeApplied(LocalDateTime.now());
        var transaction = new Transaction(TransactionType.ACCOUNT_FEE, account, ((CheckingAccount)account).getMonthlyMaintenanceFee().negate());
        transaction.setDescription("Monthly Maintenance Fee");
        transactionService.save(transaction);
        return account;
    }
*/


}
