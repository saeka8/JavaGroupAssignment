package com.example.ui.util;

import com.example.model.User;

/**
 * Manages the current user session.
 * Singleton that holds the logged-in user's information.
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Set the current logged-in user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Get the current logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Clear the session (logout).
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Check if current user is an admin.
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }
    
    /**
     * Check if current user is a teacher.
     */
    public boolean isTeacher() {
        return currentUser != null && currentUser.getRole() == User.Role.TEACHER;
    }
    
    /**
     * Check if current user is a student.
     */
    public boolean isStudent() {
        return currentUser != null && currentUser.getRole() == User.Role.STUDENT;
    }
}
