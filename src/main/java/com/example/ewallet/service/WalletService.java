package com.example.ewallet.service;

import com.example.ewallet.dto.CreateWalletRequest;
import com.example.ewallet.dto.RechargeRequest;
import com.example.ewallet.dto.TransactionResponse;
import com.example.ewallet.dto.TransferRequest;
import com.example.ewallet.dto.WalletResponse;
import com.example.ewallet.entity.TransactionType;
import com.example.ewallet.entity.Wallet;
import com.example.ewallet.entity.WalletTransaction;
import com.example.ewallet.repository.WalletRepository;
import com.example.ewallet.repository.WalletTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setOwnerName(request.getOwnerName());
        wallet.setBalance(BigDecimal.ZERO);
        Wallet saved = walletRepository.save(wallet);
        return toWalletResponse(saved);
    }

    @Transactional
    public WalletResponse recharge(Long walletId, RechargeRequest request) {
        Wallet wallet = findWalletOrThrow(walletId);
        String reference = UUID.randomUUID().toString();
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(request.getAmount());
        tx.setType(TransactionType.RECHARGE);
        tx.setDescription(request.getDescription());
        tx.setBalanceAfter(newBalance);
        tx.setReference(reference);

        walletRepository.save(wallet);
        transactionRepository.save(tx);

        return toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse transfer(TransferRequest request) {
        if (request.getFromWalletId().equals(request.getToWalletId())) {
            throw new IllegalArgumentException("From and To wallet cannot be same");
        }

        Wallet from = findWalletOrThrow(request.getFromWalletId());
        Wallet to = findWalletOrThrow(request.getToWalletId());

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source wallet");
        }

        String reference = UUID.randomUUID().toString();

        BigDecimal newFromBalance = from.getBalance().subtract(request.getAmount());
        from.setBalance(newFromBalance);

        WalletTransaction debitTx = new WalletTransaction();
        debitTx.setWallet(from);
        debitTx.setAmount(request.getAmount());
        debitTx.setType(TransactionType.TRANSFER_DEBIT);
        debitTx.setDescription(request.getDescription());
        debitTx.setReference(reference);
        debitTx.setBalanceAfter(newFromBalance);
        
        BigDecimal newToBalance = to.getBalance().add(request.getAmount());
        to.setBalance(newToBalance);

        WalletTransaction creditTx = new WalletTransaction();
        creditTx.setWallet(to);
        creditTx.setAmount(request.getAmount());
        creditTx.setType(TransactionType.TRANSFER_CREDIT);
        creditTx.setDescription(request.getDescription());
        creditTx.setReference(reference);
        creditTx.setBalanceAfter(newToBalance);

        walletRepository.saveAll(List.of(from,to));
        transactionRepository.saveAll(List.of(debitTx,creditTx));
        return toWalletResponse(from);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long walletId) {
        Wallet wallet = findWalletOrThrow(walletId);
        return toWalletResponse(wallet);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long walletId) {
        Wallet wallet = findWalletOrThrow(walletId);
        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet)
                .stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

   
    private Wallet findWalletOrThrow(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        WalletResponse resp = new WalletResponse();
        resp.setId(wallet.getId());
        resp.setOwnerName(wallet.getOwnerName());
        resp.setBalance(wallet.getBalance());
        resp.setCreatedAt(wallet.getCreatedAt());
        resp.setUpdatedAt(wallet.getUpdatedAt());
        resp.setReference(wallet.getReference());
        return resp;
    }

    private TransactionResponse toTransactionResponse(WalletTransaction tx) {
        TransactionResponse resp = new TransactionResponse();
        resp.setId(tx.getId());
        resp.setWalletId(tx.getWallet().getId());
        resp.setAmount(tx.getAmount());
        resp.setType(tx.getType());
        resp.setCreatedAt(tx.getCreatedAt());
        resp.setDescription(tx.getDescription());
        resp.setReference(tx.getReference());
        resp.setBalanceAfter(tx.getBalanceAfter());
        return resp;
    }
}
