package com.example.ewallet.service;

import com.example.ewallet.dto.LoginRequest;
import com.example.ewallet.dto.SignupRequest;
import com.example.ewallet.dto.UserProfileResponse;
import com.example.ewallet.entity.UserAccount;
import com.example.ewallet.entity.Wallet;
import com.example.ewallet.repository.UserRepository;
import com.example.ewallet.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public UserProfileResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setOwnerName(user.getUsername());
        wallet.setBalance(BigDecimal.ZERO);
        wallet = walletRepository.save(wallet);

        return toProfileResponse(user, wallet);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Wallet wallet = walletRepository.findById(user.getWallet().getId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user"));

        return toProfileResponse(user, wallet);
    }

    private UserProfileResponse toProfileResponse(UserAccount user, Wallet wallet) {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setWalletId(wallet.getId());
        resp.setWalletBalance(wallet.getBalance());
        return resp;
    }
}
