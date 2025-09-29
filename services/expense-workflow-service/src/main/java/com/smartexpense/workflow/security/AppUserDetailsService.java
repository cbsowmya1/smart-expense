package com.smartexpense.workflow.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartexpense.workflow.entities.User;
import com.smartexpense.workflow.repositories.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    public AppUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        return new CustomUserDetails(user);
    }
}
