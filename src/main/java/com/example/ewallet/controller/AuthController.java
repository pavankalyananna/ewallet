package com.example.ewallet.controller;

import com.example.ewallet.dto.LoginRequest;
import com.example.ewallet.dto.SignupRequest;
import com.example.ewallet.dto.UserProfileResponse;
import com.example.ewallet.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserProfileResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserProfileResponse resp = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfileResponse> login(@Valid @RequestBody LoginRequest request) {
        UserProfileResponse resp = authService.login(request);
        return ResponseEntity.ok(resp);
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
