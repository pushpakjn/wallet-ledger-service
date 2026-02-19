package com.wallet.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequest {
    private String referenceId; // The Idempotency Key
    private UUID sourceWalletId;
    private UUID destinationWalletId;
    private BigDecimal amount;
    private String type; // "TOPUP", "SPEND", "BONUS"
}