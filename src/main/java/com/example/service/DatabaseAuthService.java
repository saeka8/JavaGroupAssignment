package com.example.service;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;
import com.example.database.RetrieveFromDatabase;
import com.example.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Real implementation of AuthService using SQLite database.
 * Provides persistent authentication with full data persistence.
 */
public class DatabaseAuthService implements AuthService {

    private final Connection conn;

    public DatabaseAuthService() {
        // Get database connection
        this.conn = DatabaseManager.connectWithDatabase();
        if (conn == null) {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    @Override
    public Optional<User> login(String email, String password) {
        String sql = "SELECT id, name, lastname, role FROM people WHERE email='" + email.trim() + "' AND password='" + password + "'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String roleStr = rs.getString("role");

                // Convert role string to enum
                User.Role role;
                if ("admin".equalsIgnoreCase(roleStr)) {
                    role = User.Role.ADMIN;
                } else if ("teacher".equalsIgnoreCase(roleStr)) {
                    role = User.Role.TEACHER;
                } else {
                    role = User.Role.STUDENT;
                }

                User user = new User(id, email, password, name, lastname, role);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public boolean register(User user) {
        try {
            // Convert role enum to string
            String roleStr = user.getRole().toString().toLowerCase();

            // Insert into database
            InsertIntoDatabase.insertPeople(
                conn,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                roleStr
            );

            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM people WHERE email='" + email.trim() + "'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Email check error: " + e.getMessage());
        }

        return false;
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
