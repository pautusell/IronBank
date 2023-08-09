package com.ironhack.ironbank.service;

import com.ironhack.ironbank.dto.TransactionDto;
import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.model.Transaction;
import com.ironhack.ironbank.repository.AccountRepository;
import com.ironhack.ironbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDto> getLastTransactions(Long accountId){
        List<Transaction> allTransactions = transactionRepository.findAllByAccount_Id(accountId);
        var maxResults = 100;
        if (allTransactions.size()>maxResults){
            List<Transaction> lastXTransactions = new ArrayList<>();
            for (int i = 0; i < maxResults; i++) {
                lastXTransactions.add(allTransactions.get(i));
            }
            allTransactions.clear();
            allTransactions = lastXTransactions;
        }
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        for (Transaction t : allTransactions) {
            transactionDtoList.add(TransactionDto.fromTransaction(t));
        }
        return transactionDtoList;
    }

    public void save(Transaction transaction){
        transactionRepository.save(transaction);
    }

    public Optional<Transaction> findLastTransactionByType(Long accountId, TransactionType transactionType){
        return transactionRepository.findLastTransactionByType(accountId, transactionType.name());
    }

    public List<Transaction> findLastThreeUserTransactions(Long accountId){
        return transactionRepository.findLastThreeUserTransactions(accountId);
    }

    public BigDecimal averageBalanceInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod){
        return new BigDecimal(transactionRepository.averageBalanceInPeriod(accountId, startPeriod, endPeriod));
    }

    public BigDecimal sumCashOutflowsInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod){
        return new BigDecimal(transactionRepository.sumCashOutflowsInPeriod(accountId, startPeriod, endPeriod)).negate();
    }

    public BigDecimal sumCreditAmountsInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod){
        return new BigDecimal(transactionRepository.sumCreditAmountsInPeriod(accountId, startPeriod, endPeriod));
    }

    public BigDecimal avgCashOutflowsInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod){
        return new BigDecimal(transactionRepository.avgCashOutflowsInPeriod(accountId, startPeriod, endPeriod)).negate();
    }

    public List<TransactionDto> getAccountTransactionsByPeriod(Long accountId, LocalDate startDate, LocalDate endDate) {
        var listOfTransactions = transactionRepository.findAccountTransactionsByPeriod(accountId,
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        List<TransactionDto> resultListDto = new ArrayList<>();
        for (Transaction transaction : listOfTransactions) {
            resultListDto.add(TransactionDto.fromTransaction(transaction));
        }
        return resultListDto;
    }
}
