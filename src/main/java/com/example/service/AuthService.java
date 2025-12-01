package com.example.service;

import com.example.model.User;
import java.util.Optional;

/**
 * Service interface for authentication operations.
 * Person 2 will implement this with real database logic.
 */
public interface AuthService {
    
    /**
     * Attempt to log in with email and password.
     * @return Optional containing the user if successful, empty if failed
     */
    Optional<User> login(String email, String password);
    
    /**
     * Register a new user.
     * @return true if registration successful, false if email already exists
     */
    boolean register(User user);
    
    /**
     * Check if an email is already registered.
     */
    boolean emailExists(String email);
    
    /**
     * Validate password meets requirements.
     */
    boolean isValidPassword(String password);
    
    /**
     * Validate email format.
     */
    boolean isValidEmail(String email);
}
