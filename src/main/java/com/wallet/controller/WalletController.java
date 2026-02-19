package com.wallet.controller;

import com.wallet.dto.BalanceResponse;
import com.wallet.dto.TransactionRequest;
import com.wallet.model.Transaction;
import com.wallet.model.Wallet;
import com.wallet.repository.WalletRepository;
import com.wallet.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final LedgerService ledgerService;
    private final WalletRepository walletRepository;

    /**
     * Endpoint 1: Execute a Transaction (Top-up, Spend, Bonus)
     */
    @PostMapping("/transactions")
    public ResponseEntity<Transaction> executeTransaction(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody TransactionRequest request) {
        
        // Attach the key from the header to the request object before passing it down
        request.setReferenceId(idempotencyKey);
        
        Transaction completedTransaction = ledgerService.processTransaction(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(completedTransaction);
    }

    /**
     * Endpoint 2: Check Wallet Balance
     */
    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID walletId) {
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
                
        BalanceResponse response = new BalanceResponse(
                wallet.getId(), 
                wallet.getCurrency(), 
                wallet.getBalance()
        );
        
        return ResponseEntity.ok(response);
    }
}