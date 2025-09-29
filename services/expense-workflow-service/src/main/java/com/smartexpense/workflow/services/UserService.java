package com.smartexpense.workflow.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartexpense.workflow.entities.User;
import com.smartexpense.workflow.entities.enums.Role;
import com.smartexpense.workflow.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return users.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return users.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    @Transactional(readOnly = true)
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new RuntimeException("unauthenticated");
        String email = auth.getName();
        return getByEmail(email);
    }

    @Transactional
    public User create(String email, String rawPassword, Role role) {
        String normalized = email.trim().toLowerCase();
        if (users.existsByEmail(normalized)) throw new RuntimeException("email already registered");
        User u = new User();
        u.setEmail(normalized);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole(role == null ? Role.EMPLOYEE : role);
        return users.save(u);
    }

    @Transactional
    public User updateEmail(Long id, String newEmail) {
        User u = getById(id);
        String normalized = newEmail.trim().toLowerCase();
        if (!u.getEmail().equals(normalized) && users.existsByEmail(normalized))
            throw new RuntimeException("email already registered");
        u.setEmail(normalized);
        return users.save(u);
    }

    @Transactional
    public User updateRole(Long id, Role role) {
        User u = getById(id);
        u.setRole(role);
        return users.save(u);
    }

    @Transactional
    public void changePassword(Long id, String oldRawPassword, String newRawPassword) {
        User u = getById(id);
        if (!encoder.matches(oldRawPassword, u.getPasswordHash()))
            throw new RuntimeException("old password does not match");
        u.setPasswordHash(encoder.encode(newRawPassword));
        users.save(u);
    }

    @Transactional
    public void delete(Long id) {
        if (!users.existsById(id)) throw new RuntimeException("user not found");
        users.deleteById(id);
    }
}
