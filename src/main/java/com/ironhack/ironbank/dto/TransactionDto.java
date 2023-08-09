package com.ironhack.ironbank.dto;

import com.ironhack.ironbank.enums.TransactionType;
import com.ironhack.ironbank.model.Transaction;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    @NotBlank
    private Long accountId;
    private LocalDateTime timestamp;
    @NotBlank
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private Long destinationAccountId;
    private Long originAccountId;

    public static TransactionDto fromTransaction(Transaction transaction){
        var transactionDto = new TransactionDto();
        transactionDto.setAccountId(transaction.getAccount().getId());
        transactionDto.setTimestamp(transaction.getTimestamp());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTransactionType(transaction.getTransactionType());
        transactionDto.setDescription(transaction.getDescription());
        transactionDto.setDestinationAccountId(transaction.getDestinationAccountId());
        transactionDto.setOriginAccountId(transaction.getOriginAccountId());
        return transactionDto;
    }
}
