package com.smartexpense.workflow.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartexpense.workflow.dto.LoginRequest;
import com.smartexpense.workflow.dto.RegisterRequest;
import com.smartexpense.workflow.dto.RegisterResponse;
import com.smartexpense.workflow.dto.TokenResponse;
import com.smartexpense.workflow.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        RegisterResponse out = auth.register(req);
        if (out.getId() == null) {
            String msg = out.getMessage();
            int status = (msg != null && msg.toLowerCase().contains("already")) ? 409 : 400;
            return ResponseEntity.status(status).body(out);
        }
        return ResponseEntity.status(201).body(out);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        TokenResponse out = auth.login(req);
        if (out == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(out);
    }
}
