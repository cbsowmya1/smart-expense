package com.smartexpense.workflow.controllers;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartexpense.workflow.entities.User;
import com.smartexpense.workflow.entities.enums.Role;
import com.smartexpense.workflow.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    /* ---------- DTOs ---------- */

    public static class UserResponse {
        public Long id;
        public String email;
        public String role;
        public Instant createdAt;
        public UserResponse(Long id, String email, String role, Instant createdAt) {
            this.id = id; this.email = email; this.role = role; this.createdAt = createdAt;
        }
    }

    public static class ChangeEmailRequest {
        public String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ChangePasswordRequest {
        public String oldPassword;
        public String newPassword;
        public String getOldPassword() { return oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setOldPassword(String v) { this.oldPassword = v; }
        public void setNewPassword(String v) { this.newPassword = v; }
    }

    public static class UpdateRoleRequest {
        public String role;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    /* ---------- Helpers ---------- */

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getRole().name(), u.getCreatedAt());
    }

    private Role parseRole(String role) {
        if (role == null) return null;
        try { return Role.valueOf(role.toUpperCase()); } catch (Exception e) { return null; }
    }

    /* ---------- Endpoints ---------- */

    // Get the currently authenticated user, uses email from the JWT set by your JwtAuthFilter
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        User u = users.currentUser();
        return ResponseEntity.ok(toResponse(u));
    }

    // List users, admin only
   // @GetMapping
   // @PreAuthorize("hasRole('ADMIN')")
   // public ResponseEntity<List<UserResponse>> list() {
        // simple list, you can add paging later
   //     List<UserResponse> data = users
       //         .getAll() // add getAll() to UserService if you don’t have it: return users.findAll();
       //         .stream().map(this::toResponse).collect(Collectors.toList());
       // return ResponseEntity.ok(data);
    //}

    // Get any user by id, admin only
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(users.getById(id)));
    }

    // Update the signed-in user’s email
    @PutMapping("/me/email")
    public ResponseEntity<UserResponse> changeMyEmail(@RequestBody ChangeEmailRequest req) {
        if (req == null || req.email == null || req.email.isBlank()) return ResponseEntity.badRequest().build();
        Long id = users.currentUser().getId();
        return ResponseEntity.ok(toResponse(users.updateEmail(id, req.email)));
    }

    // Change the signed-in user’s password
    @PostMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(@RequestBody ChangePasswordRequest req) {
        if (req == null || req.oldPassword == null || req.newPassword == null) return ResponseEntity.badRequest().build();
        Long id = users.currentUser().getId();
        users.changePassword(id, req.oldPassword, req.newPassword);
        return ResponseEntity.noContent().build();
    }

    // Update a user’s role, admin only
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest req) {
        Role role = parseRole(req == null ? null : req.role);
        if (role == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(users.updateRole(id, role)));
    }

    // Delete a user, admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        users.delete(id);
        return ResponseEntity.noContent().build();
    }
}
