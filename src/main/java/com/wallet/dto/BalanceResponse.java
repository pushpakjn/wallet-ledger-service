package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BalanceResponse {
    private UUID walletId;
    private String currency;
    private BigDecimal balance;
}