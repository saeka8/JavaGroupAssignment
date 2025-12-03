package com.example.service;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;
import com.example.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Real implementation of UserService using SQLite database.
 */
public class DatabaseUserService implements UserService {

    private final Connection conn;

    public DatabaseUserService() {
        // Get singleton database connection for data persistence
        this.conn = DatabaseManager.getConnection();
        if (conn == null) {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, lastname, email, password, role FROM people";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return users;
    }

    @Override
    public List<User> getUsersByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        String roleStr = role.toString().toLowerCase();
        String sql = "SELECT id, name, lastname, email, password, role FROM people WHERE role='" + roleStr + "'";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return users;
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sql = "SELECT id, name, lastname, email, password, role FROM people WHERE id=" + id;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return Optional.of(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT id, name, lastname, email, password, role FROM people WHERE email='" + email + "'";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return Optional.of(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return Optional.empty();
    }

    @Override
    public List<User> searchUsers(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, lastname, email, password, role FROM people " +
                     "WHERE name LIKE '%" + query + "%' OR lastname LIKE '%" + query + "%' OR email LIKE '%" + query + "%'";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return users;
    }

    @Override
    public User createUser(User user) {
        try {
            String roleStr = user.getRole().toString().toLowerCase();
            InsertIntoDatabase.insertPeople(
                conn,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                roleStr
            );

            // Get the created user with ID
            return getUserByEmail(user.getEmail()).orElse(user);
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return user;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String roleStr = user.getRole().toString().toLowerCase();
        String sql = "UPDATE people SET name='" + user.getFirstName() + "', lastname='" + user.getLastName() +
                     "', email='" + user.getEmail() + "', password='" + user.getPassword() +
                     "', role='" + roleStr + "' WHERE id=" + user.getId();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM people WHERE id=" + id;
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    @Override
    public int countUsersByRole(User.Role role) {
        String roleStr = role.toString().toLowerCase();
        String sql = "SELECT COUNT(*) as count FROM people WHERE role='" + roleStr + "'";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error counting users by role: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return 0;
    }

    @Override
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) as count FROM people";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total user count: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return 0;
    }

    // Helper method to extract User from ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String lastname = rs.getString("lastname");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");

        User.Role role;
        if ("admin".equalsIgnoreCase(roleStr)) {
            role = User.Role.ADMIN;
        } else if ("teacher".equalsIgnoreCase(roleStr)) {
            role = User.Role.TEACHER;
        } else {
            role = User.Role.STUDENT;
        }

        return new User(id, email, password, name, lastname, role);
    }
}
