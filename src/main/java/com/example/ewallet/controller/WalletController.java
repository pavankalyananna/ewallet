package com.example.ewallet.controller;

import com.example.ewallet.dto.CreateWalletRequest;
import com.example.ewallet.dto.RechargeRequest;
import com.example.ewallet.dto.TransactionResponse;
import com.example.ewallet.dto.TransferRequest;
import com.example.ewallet.dto.WalletResponse;
import com.example.ewallet.service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse resp = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long walletId) {
        WalletResponse resp = walletService.getWallet(walletId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{walletId}/recharge")
    public ResponseEntity<WalletResponse> recharge(@PathVariable Long walletId,
                                                   @Valid @RequestBody RechargeRequest request) {
        WalletResponse resp = walletService.recharge(walletId, request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/transfer")
    public ResponseEntity<WalletResponse> transfer(@Valid @RequestBody TransferRequest request) {
        WalletResponse resp = walletService.transfer(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<TransactionResponse>> transactions(@PathVariable Long walletId) {
        List<TransactionResponse> list = walletService.getTransactions(walletId);
        return ResponseEntity.ok(list);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
