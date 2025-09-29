package com.smartexpense.workflow.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartexpense.workflow.dto.LoginRequest;
import com.smartexpense.workflow.dto.RegisterRequest;
import com.smartexpense.workflow.dto.RegisterResponse;
import com.smartexpense.workflow.dto.TokenResponse;
import com.smartexpense.workflow.entities.User;
import com.smartexpense.workflow.entities.enums.Role;
import com.smartexpense.workflow.repositories.UserRepository;
import com.smartexpense.workflow.security.JwtService;

@Service
public class AuthService {

    private final PasswordEncoder encoder;
    private final UserRepository repo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(PasswordEncoder encoder,
                       UserRepository repo,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.encoder = encoder;
        this.repo = repo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (repo.findByEmail(req.getEmail()).isPresent()) {
            RegisterResponse r = new RegisterResponse();
            r.setMessage("User already exists");
            return r;
        }

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole(Role.EMPLOYEE);
        repo.save(u);

        RegisterResponse r = new RegisterResponse();
        r.setId(u.getId());
        r.setMessage("Registered successfully");
        return r;
    }

    public TokenResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        UserDetails principal = (UserDetails) auth.getPrincipal();
        String token = jwtService.generate(principal);
        return new TokenResponse(token);
    }
}
