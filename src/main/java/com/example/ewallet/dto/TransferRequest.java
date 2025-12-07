package com.example.ewallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {

    @NotNull
    private Long fromWalletId;

    @NotNull
    private Long toWalletId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;

    public Long getFromWalletId() {
        return fromWalletId;
    }

    public void setFromWalletId(Long fromWalletId) {
        this.fromWalletId = fromWalletId;
    }

    public Long getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(Long toWalletId) {
        this.toWalletId = toWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
