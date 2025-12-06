package com.example.ewallet.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateWalletRequest {

    @NotBlank
    private String ownerName;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
