package com.ironhack.ironbank.service;

import com.ironhack.ironbank.dto.*;
import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.exception.OperationalException;
import com.ironhack.ironbank.model.*;
import com.ironhack.ironbank.repository.AccountRepository;
import com.ironhack.ironbank.utils.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ValidationService validationService;
    private final FraudService fraudService;
    private final TransactionService transactionService;


    public Account findAccount(Long accountId){
        return accountRepository.findById(accountId).orElseThrow();
    }

    public CheckBalanceResponse checkBalance (Long accountId) {
        var account = findAccount(accountId);
        account = validationService.checkOwnerAndConditions(account);
        var response = new CheckBalanceResponse();
        response.setBalance(account.getBalance().getAmount());
        return response;
    }

    public AccountStatement getAccountStatement(Long accountId){
        var account = findAccount(accountId);
        account = validationService.checkOwnerAndConditions(account);
        var accountStatement = new AccountStatement();
        accountStatement.setAccountId(account.getId());
        accountStatement.setTypeOfAccount(account.getAccountType());
        accountStatement.setBalance(account.getBalance().getAmount());
        accountStatement.setStatus(account.getStatus());
        accountStatement.setPrimaryOwnerName(account.getPrimaryOwner().getName());
        accountStatement.setSecondaryOwnerName(accountStatement.getSecondaryOwnerName());
        accountStatement.setBalance(account.getBalance().getAmount());
        accountStatement.setLastTransactions(transactionService.getLastTransactions(accountId));
        return accountStatement;
    }

    public AccountDto createAccount(AccountDto accountDto){
        var account = new Account();
        var accountHolder = (AccountHolder) userService.findByUsername(accountDto.getPrimaryOwnerUsername());
        switch (accountDto.getAccountType()){
            case CHECKING -> {
                if (Period.between(accountHolder.getDob(), LocalDate.now()).getYears() < 24) {
                    account = new StudentCheckingAccount();
                } else account = new CheckingAccount();
            }
            case SAVINGS -> account = new SavingsAccount();
            case CREDIT_CARD -> account = new CreditCardAccount();
            case STUDENT_CHECKING -> account = new StudentCheckingAccount();
        }
        account.setBalance(new Money(accountDto.getBalance()));
        account.setPrimaryOwner(accountHolder);
        if(accountDto.getSecondaryOwnerUsername() != null) {
            account.setSecondaryOwner((AccountHolder) userService.findByUsername(accountDto.getSecondaryOwnerUsername()));
        }
        return AccountDto.fromAccount(accountRepository.save(account));
    }

    public WithdrawalResponse withdrawal(Long accountId, BigDecimal amount){
        var account = findAccount(accountId);
        if (account.getAccountType().equals(AccountType.CHECKING) || account.getAccountType().equals(AccountType.STUDENT_CHECKING)) {
            account = validationService.checkOwnerAndConditions(account);
            account = validationService.checkEnoughBalance(account, amount);
            account = fraudService.fraudValidations(account, amount);
            account = validationService.checkAccountActive(account);
            account.getBalance().decreaseAmount(amount);
            var transaction = new Transaction(TransactionType.WITHDRAWAL, account, amount.negate());
            transactionService.save(transaction);
            account = validationService.checkBalanceConditions(account);
            accountRepository.save(account);
            return new WithdrawalResponse("Transaction Successful! Current balance: " + account.getBalance().getAmount());
        } else throw new OperationalException("This operation is not allowed for this account type");
    }

    public DepositResponse deposit(Long accountId, BigDecimal amount){
        var account = findAccount(accountId);
        if (account.getAccountType().equals(AccountType.CHECKING) || account.getAccountType().equals(AccountType.STUDENT_CHECKING)) {
            account = validationService.checkOwnerAndConditions(account);
            account.getBalance().increaseAmount(amount);
            var transaction = new Transaction(TransactionType.DEPOSIT, account, amount);
            transactionService.save(transaction);
            accountRepository.save(account);
            return new DepositResponse("Transaction Successful! Current balance: " + account.getBalance().getAmount());
        } else throw new OperationalException("This operation is not allowed for this account type");
    }

    public TransferResponse transfer(Long originAccountId, BigDecimal amount, Long destinationAccountId, String description){
        var originAccount = findAccount(originAccountId);
        if (!originAccount.getAccountType().equals(AccountType.CREDIT_CARD)) {
            originAccount = validationService.checkOwnerAndConditions(originAccount);
            originAccount = validationService.checkEnoughBalance(originAccount, amount);
            originAccount = fraudService.fraudValidations(originAccount, amount);
            originAccount = validationService.checkAccountActive(originAccount);
            originAccount.getBalance().decreaseAmount(amount);
            var transactionOrigin = new Transaction(TransactionType.TRANSFER, originAccount, amount.negate());
            transactionOrigin.setDestinationAccountId(destinationAccountId);
            transactionOrigin.setDescription(description);
            transactionService.save(transactionOrigin);
            originAccount = validationService.checkBalanceConditions(originAccount);
            accountRepository.save(originAccount);

            var isDestinationAccountInternal = lookUpDestinationAccount(destinationAccountId);
            String destinationAccountIdResponse = String.valueOf(destinationAccountId);
            if (isDestinationAccountInternal) {
                var destinationAccount = findAccount(destinationAccountId);
                destinationAccount = validationService.checkBalanceConditions(destinationAccount);
                destinationAccount.getBalance().increaseAmount(amount);
                var transactionDestination = new Transaction(TransactionType.TRANSFER, destinationAccount, amount);
                transactionDestination.setOriginAccountId(originAccountId);
                transactionDestination.setDescription(description);
                transactionService.save(transactionDestination);
                accountRepository.save(destinationAccount);
            } else destinationAccountIdResponse = destinationAccountIdResponse + " (EXTERNAL Account)";
            return new TransferResponse(originAccountId, amount, destinationAccountIdResponse, description, transactionOrigin.getTimestamp());
        } else throw new OperationalException("This operation is not allowed for this account type");
    }

    public boolean lookUpDestinationAccount(Long destinationAccountId){
        var foundInDb = accountRepository.findById(destinationAccountId);
        return foundInDb.isPresent();
    }

    public CreditPurchaseResponse creditCardPurchase(Long accountId, String cardSecretKey, BigDecimal amount, String merchantDescription) {
        var account = findAccount(accountId);
        if (account.getAccountType().equals(AccountType.CREDIT_CARD)) {
            account = validationService.checkSecretKey(account, cardSecretKey);
            account = validationService.checkCreditLimit(account, amount);
            account = fraudService.fraudValidations(account, amount);
            account = validationService.checkAccountActive(account);
            account.getBalance().increaseAmount(amount);
            var transaction = new Transaction(TransactionType.CREDIT_PURCHASE, account, amount);
            transaction.setDescription(merchantDescription);
            transactionService.save(transaction);
            accountRepository.save(account);
            return new CreditPurchaseResponse("Payment accepted");
        } else throw new OperationalException("This operation is not allowed for this account type");
    }

    public CreditWithdrawalResponse creditCardWithdrawal(Long accountId, BigDecimal amount) {
        var account = findAccount(accountId);
        if (account.getAccountType().equals(AccountType.CREDIT_CARD)) {
            account = validationService.checkOwnerOrAdmin(account);
            account = validationService.checkCreditLimit(account, amount);
            account = fraudService.fraudValidations(account, amount);
            account = validationService.checkAccountActive(account);
            account.getBalance().increaseAmount(amount);
            var transaction = new Transaction(TransactionType.CREDIT_WITHDRAWAL, account, amount);
            transaction.setDescription("Credit Card Withdrawal");
            transactionService.save(transaction);
            accountRepository.save(account);
            return new CreditWithdrawalResponse("Withdrawal on credit accepted. Current credit Balance: "
                    + account.getBalance().getAmount() + " out of " + ((CreditCardAccount)account).getCreditLimit());
        } else throw new OperationalException("This operation is not allowed for this account type");
    }


    public ThirdPartyOpResponse thirdPartyCharge(String hashedKey, Long accountId, String secretKey, BigDecimal amount, String concept) {
        validationService.checkRegisteredTP(hashedKey);
        var account = findAccount(accountId);
        if (account.getAccountType().equals(AccountType.CHECKING) || account.getAccountType().equals(AccountType.STUDENT_CHECKING)) {
            account = validationService.checkSecretKey(account, secretKey);
            account = validationService.checkBalanceConditions(account);
            account = validationService.checkEnoughBalance(account, amount);
            account = fraudService.fraudValidations(account, amount);
            account = validationService.checkAccountActive(account);
            account.getBalance().decreaseAmount(amount);
            var transaction = new Transaction(TransactionType.TP_TRANSACTION, account, amount.negate());
            transaction.setDescription(concept);
            transactionService.save(transaction);
            account = validationService.checkBalanceConditions(account);
            accountRepository.save(account);
            return new ThirdPartyOpResponse("Operation successful with transaction id: " + transaction.getId());
        } else throw new OperationalException("This operation is not allowed for this account type");
    }

    public ThirdPartyOpResponse thirdPartyDeposit(String hashedKey, Long accountId, BigDecimal amount, String concept) {
        validationService.checkRegisteredTP(hashedKey);
        var account = findAccount(accountId);
        if (account.getAccountType().equals(AccountType.CHECKING) || account.getAccountType().equals(AccountType.STUDENT_CHECKING)) {
            account = validationService.checkBalanceConditions(account);
            account.getBalance().increaseAmount(amount);
            var transaction = new Transaction(TransactionType.TP_TRANSACTION, account, amount);
            transaction.setDescription(concept);
            transactionService.save(transaction);
            accountRepository.save(account);
            return new ThirdPartyOpResponse("Operation successful with transaction id: " + transaction.getId());
        } else throw new OperationalException("This operation is not allowed for this account type");
    }

    public AccountFullDto editAccountConditions(Long accountId, Optional<BigDecimal> penaltyFee, Optional<BigDecimal> minimumBalance,
                                                Optional<BigDecimal> monthlyMaintenanceFee, Optional<BigDecimal> interestRate,
                                                Optional<BigDecimal> creditLimit, Optional<Status> status) {

        var accountToUpdate = findAccount(accountId);
        var accountFullDto = new AccountFullDto();
        switch (accountToUpdate.getAccountType()){
            case CHECKING -> {
                if (minimumBalance.isPresent()){
                    ((CheckingAccount) accountToUpdate).setMinimumBalance(minimumBalance.get());
                    accountFullDto.setMinimumBalance(minimumBalance.get());
                }
                if (monthlyMaintenanceFee.isPresent()){
                    ((CheckingAccount) accountToUpdate).setMonthlyMaintenanceFee(monthlyMaintenanceFee.get());
                    accountFullDto.setMonthlyMaintenanceFee(monthlyMaintenanceFee.get());
                }
            }
            case SAVINGS -> {
                if (interestRate.isPresent()){
                    ((SavingsAccount) accountToUpdate).setInterestRate(interestRate.get());
                    accountFullDto.setInterestRate(interestRate.get());
                }
                if (minimumBalance.isPresent()){
                    ((SavingsAccount) accountToUpdate).setMinimumBalance(minimumBalance.get());
                    accountFullDto.setMinimumBalance(minimumBalance.get());
                }
            }
            case CREDIT_CARD -> {
                if (creditLimit.isPresent()){
                    ((CreditCardAccount) accountToUpdate).setCreditLimit(creditLimit.get());
                    accountFullDto.setCreditLimit(creditLimit.get());
                }
                if (interestRate.isPresent()){
                    ((CreditCardAccount) accountToUpdate).setInterestRate(interestRate.get());
                    accountFullDto.setInterestRate(interestRate.get());
                }
            }
            case STUDENT_CHECKING -> {
            }
        }

        if (penaltyFee.isPresent()){
            accountToUpdate.setPenaltyFee(penaltyFee.get());
            accountFullDto.setPenaltyFee(penaltyFee.get());
        }

        if (status.isPresent()){
            accountToUpdate.setStatus(status.get());
            accountFullDto.setStatus(status.get());
        }
        try {
            accountRepository.save(accountToUpdate);
        } catch (Exception e) {
            throw new OperationalException("Parameter out of the allowed boundaries for this type of account");
        }
        return accountFullDto;
    }

    public AccountDto editSecondaryOwner(Long accountId, String secondaryAccountHolderUsername) {
        var accountToUpdate = findAccount(accountId);
        var secondaryAH = userService.findByUsername(secondaryAccountHolderUsername);
        accountToUpdate.setSecondaryOwner((AccountHolder) secondaryAH);
        return AccountDto.fromAccount(accountRepository.save(accountToUpdate));
    }

    public AccountDto editBalance(Long accountId, BigDecimal newBalance) {
        var accountToUpdate = findAccount(accountId);
        var difference = newBalance.subtract(accountToUpdate.getBalance().getAmount());
        accountToUpdate.getBalance().increaseAmount(difference);
        var transaction = new Transaction(TransactionType.ACCOUNT_CORRECTION, accountToUpdate, difference);
        transaction.setDescription("Manual correction by the Administrator");
        transactionService.save(transaction);
        return AccountDto.fromAccount(accountRepository.save(accountToUpdate));
    }

    public String deleteAccount(Long accountId) {
        var accountToDelete = findAccount(accountId);
        if (accountToDelete.getBalance().getAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new OperationalException("The Account " + accountId + " still has funds. Please empty the balance before deletion");
        } else {
            accountRepository.delete(accountToDelete);
        }
        return "Account " + accountId + " deleted successfully";
    }

    public List<AccountDto> getOverviewOfAccountsByType(AccountType accountType) {
        var listOfAllAccountsByType = accountRepository.findAllByAccountType(accountType.name());
        List<AccountDto> listOfResultsDto = new ArrayList<>();
        for (Account account : listOfAllAccountsByType){
            listOfResultsDto.add(AccountDto.fromAccount(account));
        }
        return listOfResultsDto;
    }
}
