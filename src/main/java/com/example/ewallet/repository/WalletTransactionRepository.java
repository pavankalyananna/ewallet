package com.example.ewallet.repository;

import com.example.ewallet.entity.WalletTransaction;
import com.example.ewallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
}
