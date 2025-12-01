package com.example.service;

import com.example.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations.
 * Person 2 will implement this with real database logic.
 */
public interface UserService {
    
    /**
     * Get all users in the system.
     */
    List<User> getAllUsers();
    
    /**
     * Get users filtered by role.
     */
    List<User> getUsersByRole(User.Role role);
    
    /**
     * Get a user by ID.
     */
    Optional<User> getUserById(int id);
    
    /**
     * Get a user by email.
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Search users by name or email.
     */
    List<User> searchUsers(String query);
    
    /**
     * Create a new user.
     * @return the created user with assigned ID
     */
    User createUser(User user);
    
    /**
     * Update an existing user.
     */
    boolean updateUser(User user);
    
    /**
     * Delete a user by ID.
     */
    boolean deleteUser(int id);
    
    /**
     * Get count of users by role.
     */
    int countUsersByRole(User.Role role);
    
    /**
     * Get total user count.
     */
    int getTotalUserCount();
}
