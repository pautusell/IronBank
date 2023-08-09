package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
    @CreationTimestamp
    private LocalDateTime timestamp;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String description;
    private Long destinationAccountId;
    private Long originAccountId;
    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    public Transaction (TransactionType transactionType, Account account, BigDecimal amount){
        setTransactionType(transactionType);
        setAccount(account);
        setAmount(amount);
        setCurrentBalance(account.getBalance().getAmount());
    }
}
