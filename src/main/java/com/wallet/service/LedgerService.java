package com.wallet.service;

import com.wallet.dto.TransactionRequest;
import com.wallet.exception.InsufficientBalanceException;
import com.wallet.model.Transaction;
import com.wallet.model.Wallet;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction processTransaction(TransactionRequest request) {
        
        // 1. IDEMPOTENCY CHECK: Prevent double-charging
        Optional<Transaction> existingTx = transactionRepository.findByReferenceId(request.getReferenceId());
        if (existingTx.isPresent()) {
            return existingTx.get(); // Instantly return the previous success
        }

        // 2. DEADLOCK AVOIDANCE: Order the locks mathematically
        UUID firstLockId = request.getSourceWalletId().compareTo(request.getDestinationWalletId()) < 0 
                ? request.getSourceWalletId() : request.getDestinationWalletId();
        UUID secondLockId = request.getSourceWalletId().compareTo(request.getDestinationWalletId()) < 0 
                ? request.getDestinationWalletId() : request.getSourceWalletId();

        // 3. CONCURRENCY: Pessimistic Locking (Row-level locks)
        Wallet firstWallet = walletRepository.findByIdForUpdate(firstLockId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + firstLockId));
        Wallet secondWallet = walletRepository.findByIdForUpdate(secondLockId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + secondLockId));

        // Reassign to Source and Destination variables to keep business logic clear
        Wallet sourceWallet = firstWallet.getId().equals(request.getSourceWalletId()) ? firstWallet : secondWallet;
        Wallet destWallet = firstWallet.getId().equals(request.getDestinationWalletId()) ? firstWallet : secondWallet;

        // 4. BALANCE VALIDATION
        if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source wallet"); // <-- This triggers the 400 Bad Request!
        }

        // 5. DOUBLE-ENTRY LEDGER: Move the money
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(request.getAmount()));
        
        
        
        
        destWallet.setBalance(destWallet.getBalance().add(request.getAmount()));

        walletRepository.save(sourceWallet);
        
        //check @Transactional/@lock working or not by throwing error in between
//        if (true) {
//            throw new RuntimeException("Simulated Server Crash! The power went out!");
//        }
        
        walletRepository.save(destWallet);

        // 6. RECORD THE AUDIT TRAIL
        Transaction newTx = Transaction.builder()
                .referenceId(request.getReferenceId())
                .sourceWalletId(sourceWallet.getId())
                .destinationWalletId(destWallet.getId())
                .amount(request.getAmount())
                .type(request.getType())
                .status("SUCCESS")
                .build();

        return transactionRepository.save(newTx);
    }
}