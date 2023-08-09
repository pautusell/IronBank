package com.ironhack.ironbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class TransferResponse {
    private Long originAccountId;
    private BigDecimal amount;
    private String destinationAccountId;
    private String description;
    private LocalDateTime timeStamp;
}
