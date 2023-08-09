package com.ironhack.ironbank.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CheckBalanceResponse {
    private BigDecimal balance;
}
