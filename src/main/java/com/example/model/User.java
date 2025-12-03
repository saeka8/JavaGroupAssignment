package com.example.model;

/**
 * Represents a user in the Quiz Platform.
 * Supports three roles: ADMIN, TEACHER, STUDENT.
 */
public abstract class User {
    
    public enum Role {
        ADMIN, TEACHER, STUDENT
    }
    
    private int id;
    private String email;
    private String password; // In production, this would be hashed
    private String firstName;
    private String lastName;
    private Role role;
    
    protected User(int id, String email, String password, String firstName, String lastName, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    protected User(String email, String password, String firstName, String lastName, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    /**
     * Factory helpers to create role-specific user instances.
     */
    public static User createUser(int id, String email, String password, String firstName, String lastName, Role role) {
        return switch (role) {
            case ADMIN -> new AdminUser(id, email, password, firstName, lastName);
            case TEACHER -> new TeacherUser(id, email, password, firstName, lastName);
            case STUDENT -> new StudentUser(id, email, password, firstName, lastName);
        };
    }

    public static User createUser(String email, String password, String firstName, String lastName, Role role) {
        return createUser(0, email, password, firstName, lastName, role);
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    // Utility method
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + getFullName() + '\'' +
                ", role=" + role +
                '}';
    }
}
