package com.example.service.mock;

import com.example.model.User;
import com.example.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of UserService for UI testing.
 * Replace with real implementation when Person 2 completes their work.
 */
public class MockUserService implements UserService {
    
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;
    
    public MockUserService() {
        // Pre-populate with test data
        addUser(new User("admin@quiz.com", "admin123", "Admin", "User", User.Role.ADMIN));
        addUser(new User("teacher@quiz.com", "teacher123", "John", "Smith", User.Role.TEACHER));
        addUser(new User("teacher2@quiz.com", "teacher123", "Sarah", "Johnson", User.Role.TEACHER));
        addUser(new User("student@quiz.com", "student123", "Jane", "Doe", User.Role.STUDENT));
        addUser(new User("student2@quiz.com", "student123", "Mike", "Brown", User.Role.STUDENT));
        addUser(new User("student3@quiz.com", "student123", "Emily", "Davis", User.Role.STUDENT));
        addUser(new User("student4@quiz.com", "student123", "Chris", "Wilson", User.Role.STUDENT));
        addUser(new User("student5@quiz.com", "student123", "Anna", "Martinez", User.Role.STUDENT));
    }
    
    private void addUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
    }
    
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public List<User> getUsersByRole(User.Role role) {
        return users.values().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    @Override
    public List<User> searchUsers(String query) {
        String lowerQuery = query.toLowerCase();
        return users.values().stream()
                .filter(u -> u.getFullName().toLowerCase().contains(lowerQuery) ||
                            u.getEmail().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    
    @Override
    public User createUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public boolean updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean deleteUser(int id) {
        return users.remove(id) != null;
    }
    
    @Override
    public int countUsersByRole(User.Role role) {
        return (int) users.values().stream()
                .filter(u -> u.getRole() == role)
                .count();
    }
    
    @Override
    public int getTotalUserCount() {
        return users.size();
    }
}
