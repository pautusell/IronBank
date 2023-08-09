package com.ironhack.ironbank.demo;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.model.*;
import com.ironhack.ironbank.repository.AccountRepository;
import com.ironhack.ironbank.repository.TransactionRepository;
import com.ironhack.ironbank.repository.UserRepository;
import com.ironhack.ironbank.utils.Money;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Transactional
@RequiredArgsConstructor
@Log
@Profile("demo")
public class DataLoader {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void bankTestUsers(){
        log.info("Starting demo users loading...");
        var user1 = new AccountHolder("Test User", "user", passwordEncoder.encode("user"), "ROLE_USER", LocalDate.parse("1986-04-16"),"user@test.com");
        userRepository.save(user1);
        //userService.newAccountHolder(AccountHolderDto.fromAccountHolder(user1));
        log.info("User " + user1.getUsername() + " was created successfully");

        var user2 = new Admin("Test Admin", "admin", passwordEncoder.encode("admin"), "ROLE_USER,ROLE_ADMIN");
        userRepository.save(user2);
        //userService.newAdmin(AdminDto.fromAdmin(user2));
        log.info("User " + user2.getUsername() + " was created successfully");
        log.info("Demo users loading successful.");

        log.info("Starting demo DATA loading...");

        var nameList = List.of("Mike", "Sofia", "Peter", "Joshua", "Margaret", "Simone", "Ralph", "Anthony");
        Random random = new Random();
        List<AccountHolder> listOfDemoUsers = new ArrayList<>();
        for(String name : nameList) {
            String DNI = String.valueOf(random.nextInt(10000000, 99999999)) + (char) ('A' + random.nextInt(26));
            var nextUser = new AccountHolder(name+" AH", DNI, passwordEncoder.encode(name + 1234),
                    "ROLE_USER", LocalDate.parse(random.nextInt(1930, 2007)+"-04-16"), name + "@test.com");
            userRepository.save(nextUser);
            listOfDemoUsers.add(nextUser);
            if (nextUser.getDob().getYear() > 1998) {
                var welcomeCheckingAccount = new StudentCheckingAccount(new Money(BigDecimal.valueOf(random.nextInt(10,3000))), nextUser);
                accountRepository.save(welcomeCheckingAccount);
            } else {
                var welcomeCheckingAccount = new CheckingAccount(new Money(BigDecimal.valueOf(random.nextInt(100,10000))), nextUser);
                accountRepository.save(welcomeCheckingAccount);
            }

        }
        log.info("Generation of DEMO Account Holders and CheckingAccounts successful.");


        for(AccountHolder accountHolder : listOfDemoUsers) {
            switch (random.nextInt(1,5)){
                case 1 -> accountRepository.save(new SavingsAccount(new Money(BigDecimal.valueOf(random.nextInt(1000,50000))), accountHolder));
                case 2 -> accountRepository.save(new CreditCardAccount(new Money(BigDecimal.valueOf(0)), accountHolder));
                case 3 -> {
                    accountRepository.save(new SavingsAccount(new Money(BigDecimal.valueOf(random.nextInt(1000,50000))), accountHolder));
                    accountRepository.save(new CreditCardAccount(new Money(BigDecimal.valueOf(0)), accountHolder));
                }
                case 4 -> {}   //only checkingAcc
            }
        }
        log.info("Generation of DEMO additional accounts successful.");


        List<Account> listOfAllChecking = new ArrayList<>();
        listOfAllChecking.addAll(accountRepository.findAllByAccountType(AccountType.CHECKING.name()));
        listOfAllChecking.addAll(accountRepository.findAllByAccountType(AccountType.STUDENT_CHECKING.name()));

        for (Account account : listOfAllChecking) {
            var roof = random.nextInt(50);
            List<String> typeList = new ArrayList<>(List.of(
                    "DEPOSIT",
                    "WITHDRAWAL",
                    "TRANSFER",
                    "TP_TRANSACTION"));
            for (int i = 0; i < roof; i++) {
                var transactionType = TransactionType.valueOf(typeList.get(random.nextInt(0, typeList.size())));
                switch (transactionType){
                    case WITHDRAWAL -> {
                        var amount = BigDecimal.valueOf(random.nextInt(500));
                        if(account.getBalance().getAmount().compareTo(amount) >= 0) {
                            account.getBalance().decreaseAmount(amount);
                            accountRepository.save(account);
                            var nextTransaction = new Transaction(transactionType, account, amount.negate());
                            transactionRepository.save(nextTransaction);
                        }
                    }
                    case DEPOSIT -> {
                        var amount = BigDecimal.valueOf(random.nextInt(800));
                        account.getBalance().increaseAmount(amount);
                        accountRepository.save(account);
                        var nextTransaction = new Transaction(transactionType, account, amount);
                        transactionRepository.save(nextTransaction);
                    }
                    case TRANSFER -> {
                        var amount = BigDecimal.valueOf(random.nextInt(1000));
                        if(account.getBalance().getAmount().compareTo(amount) >= 0) {
                            account.getBalance().decreaseAmount(amount);
                            accountRepository.save(account);
                            var nextTransaction = new Transaction(transactionType, account, amount.negate());
                            nextTransaction.setDestinationAccountId(8888888888888888L);
                            nextTransaction.setDescription("Demo Transfer");
                            transactionRepository.save(nextTransaction);
                        }
                    }
                    case TP_TRANSACTION -> {
                        var amount = BigDecimal.valueOf(random.nextInt(600)-300);
                        if(account.getBalance().getAmount().compareTo(amount) >= 0) {
                            account.getBalance().decreaseAmount(amount);
                            accountRepository.save(account);
                            var nextTransaction = new Transaction(transactionType, account, amount.negate());
                            nextTransaction.setDescription("Demo Third Party transaction");
                            transactionRepository.save(nextTransaction);
                        }
                    }
                }
            }
        }

        var listOfCreditCardAccounts = accountRepository.findAllByAccountType(AccountType.CREDIT_CARD.name());
        for (Account account : listOfCreditCardAccounts) {
            var roof = random.nextInt(100);
            List<String> typeList = new ArrayList<>(List.of(
                    "CREDIT_PURCHASE",
                    "CREDIT_WITHDRAWAL"));
            for (int i = 0; i < roof; i++) {
                var amount = BigDecimal.valueOf(random.nextInt(30));
                if(account.getBalance().getAmount().add(amount).compareTo(((CreditCardAccount)account).getCreditLimit()) <= 0) {
                    account.getBalance().increaseAmount(amount);
                    accountRepository.save(account);
                    var transactionType = TransactionType.valueOf(typeList.get(random.nextInt(0, typeList.size())));
                    var nextTransaction = new Transaction(transactionType, account, amount);
                    nextTransaction.setDescription("Demo CreditCard Use");
                    transactionRepository.save(nextTransaction);
                }
            }
        }

        var listOfSavingsAccounts = accountRepository.findAllByAccountType(AccountType.SAVINGS.name());
        for (Account account : listOfSavingsAccounts) {
            var roof = random.nextInt(30);
            for (int i = 0; i < roof; i++) {
                var amount = BigDecimal.valueOf(random.nextInt(6000)-3000);
                if(account.getBalance().getAmount().compareTo(amount) >= 0) {
                    account.getBalance().decreaseAmount(amount);
                    accountRepository.save(account);
                    var nextTransaction = new Transaction(TransactionType.TRANSFER, account, amount.negate());
                    nextTransaction.setDescription("Transfer of Savings");
                    transactionRepository.save(nextTransaction);
                }
            }
        }

        log.info("Generation of DEMO Account Holders and CheckingAccounts successful.");
        log.info("Finished DEMO data loading.");


        /*
        Por qué "detached entity passed to persist" AccountHolder.
        Si le pongo Transactional, va.

         */

    }

}
