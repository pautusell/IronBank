package com.ironhack.ironbank.repository;

import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccount_Id(Long accountId);

    @Query(value = "SELECT * FROM transactions t WHERE account_id = ?1 AND transaction_type = ?2 ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Transaction> findLastTransactionByType(Long accountId, String transactionType);

    @Query(value = "SELECT * FROM transactions t WHERE account_id = ?1 AND transaction_type NOT LIKE 'ACCOUNT_%' ORDER BY id DESC LIMIT 3", nativeQuery = true)
    List<Transaction> findLastThreeUserTransactions(Long accountId);

    @Query(value = "SELECT FORMAT(AVG(t.current_balance),0,'es_ES') FROM transactions t WHERE account_id = ?1 AND timestamp BETWEEN ?2 AND ?3", nativeQuery = true)
    String averageBalanceInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod);

    @Query(value = "SELECT FORMAT(SUM(a.amount),0,'es_ES') FROM (SELECT t.amount FROM transactions t WHERE account_id = ?1 AND timestamp BETWEEN ?2 AND ?3 AND transaction_type NOT LIKE 'ACCOUNT_%') a WHERE amount < 0", nativeQuery = true)
    String sumCashOutflowsInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod);

    @Query(value = "SELECT FORMAT(SUM(a.amount),0,'es_ES') FROM (SELECT t.amount FROM transactions t WHERE account_id = ?1 AND timestamp BETWEEN ?2 AND ?3 AND transaction_type LIKE 'CREDIT_%') a WHERE amount > 0", nativeQuery = true)
    String sumCreditAmountsInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod);

    @Query(value = "SELECT FORMAT(AVG(a.amount),0,'es_ES') FROM (SELECT t.amount FROM transactions t WHERE account_id = ?1 AND timestamp BETWEEN ?2 AND ?3 AND transaction_type NOT LIKE 'ACCOUNT_%') a WHERE amount < 0", nativeQuery = true)
    String avgCashOutflowsInPeriod(Long accountId, LocalDateTime startPeriod, LocalDateTime endPeriod);

    @Query(value = "SELECT * FROM transactions t WHERE account_id = ?1 AND timestamp BETWEEN ?2 AND ?3", nativeQuery = true)
    List<Transaction> findAccountTransactionsByPeriod(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
