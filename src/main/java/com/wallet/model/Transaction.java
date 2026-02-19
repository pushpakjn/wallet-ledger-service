package com.wallet.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference_id", unique = true, nullable = false)
    private String referenceId; // The Idempotency Key

    @Column(name = "source_wallet_id")
    private UUID sourceWalletId;

    @Column(name = "destination_wallet_id")
    private UUID destinationWalletId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String type; // TOPUP, BONUS, PURCHASE

    @Column(nullable = false, length = 20)
    private String status; // PENDING, SUCCESS, FAILED

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}