package com.example.service.mock;

import com.example.model.User;
import com.example.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock implementation of AuthService for UI testing.
 * Replace with real implementation when Person 2 completes their work.
 */
public class MockAuthService implements AuthService {
    
    private final Map<String, User> users = new HashMap<>();
    private int nextId = 4;
    
    public MockAuthService() {
        // Pre-populate with test users (one of each role)
        users.put("admin@quiz.com", new User(1, "admin@quiz.com", "admin123", "Admin", "User", User.Role.ADMIN));
        users.put("teacher@quiz.com", new User(2, "teacher@quiz.com", "teacher123", "John", "Teacher", User.Role.TEACHER));
        users.put("student@quiz.com", new User(3, "student@quiz.com", "student123", "Jane", "Student", User.Role.STUDENT));
    }
    
    @Override
    public Optional<User> login(String email, String password) {
        User user = users.get(email.toLowerCase().trim());
        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
    
    @Override
    public boolean register(User user) {
        String email = user.getEmail().toLowerCase().trim();
        if (users.containsKey(email)) {
            return false; // Email already exists
        }
        user.setId(nextId++);
        users.put(email, user);
        return true;
    }
    
    @Override
    public boolean emailExists(String email) {
        return users.containsKey(email.toLowerCase().trim());
    }
    
    @Override
    public boolean isValidPassword(String password) {
        // At least 5 characters
        return password != null && password.length() >= 5;
    }
    
    @Override
    public boolean isValidEmail(String email) {
        // Simple validation: contains @ and .
        return email != null && email.contains("@") && email.contains(".");
    }
}
