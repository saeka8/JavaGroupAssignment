package com.example.database;

import com.example.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static com.example.model.User.Role.*;

public class RetrieveFromDatabase {

    // ===== Reading data =====
    // Retrieve a user based on email and password
    private static User retrieveUser(Connection conn, String email, String password) throws SQLException {
        String sql = "SELECT id, name, lastname, role FROM people WHERE email='" + email + "' AND password='" + password + "'";
        User user = null;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { // executeQuery returns data
            // Loop through the result set. rs.next() returns false when there are no more rows.
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String role = rs.getString("role");
                User.Role user_role;
                if (role.equalsIgnoreCase("admin") ){
                    user_role = ADMIN;
                } else if (role.equalsIgnoreCase("teacher")) {
                    user_role = TEACHER;
                }else {
                    user_role = STUDENT;
                }
                user = new User(id,email,password,name,lastname,user_role);
            }
        }
        return user;
    }

    // Retrieve Groups based on Teacher ID
    private static Map<Integer,String> retrieveGroup(Connection conn, User teacher) throws SQLException {
        int teacher_id = teacher.getId();
        Map<Integer,String> groups = new HashMap<>();
        String sql = "SELECT id, name FROM groups WHERE teacher_id=" + teacher_id;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { // executeQuery returns data
            // Loop through the result set. rs.next() returns false when there are no more rows.
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                groups.put(id,name);
            }
        }
        return groups;
    }
}
