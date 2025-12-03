package com.example.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;
import com.example.model.Group;
import com.example.model.User;

/**
 * Real implementation of GroupService using SQLite database.
 */
public class DatabaseGroupService implements GroupService {

    private final Connection conn;

    public DatabaseGroupService() {
        this.conn = DatabaseManager.connectWithDatabase();
        if (conn == null) {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT id, name, teacher_id FROM groups";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                groups.add(extractGroupFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all groups: " + e.getMessage());
        }

        return groups;
    }

    @Override
    public List<Group> getGroupsByTeacher(int teacherId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT id, name, teacher_id FROM groups WHERE teacher_id=" + teacherId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                groups.add(extractGroupFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting groups by teacher: " + e.getMessage());
        }

        return groups;
    }

    @Override
    public Optional<Group> getGroupById(int id) {
        String sql = "SELECT id, name, teacher_id FROM groups WHERE id=" + id;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return Optional.of(extractGroupFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting group by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Group> getGroupByName(String name) {
        String sql = "SELECT id, name, teacher_id FROM groups WHERE name='" + name + "'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return Optional.of(extractGroupFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting group by name: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Group createGroup(Group group) {
        try {
            InsertIntoDatabase.insertGroup(
                conn,
                group.getName(),
                group.getTeacherId()
            );

            // Get the created group with ID
            return getGroupByName(group.getName()).orElse(group);
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
            return group;
        }
    }

    @Override
    public boolean updateGroup(Group group) {
        String sql = "UPDATE groups SET name='" + group.getName() +
                    "', teacher_id=" + group.getTeacherId() +
                    " WHERE id=" + group.getId();

        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating group: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteGroup(int id) {
        // First delete all enrollments for this group
        String deleteEnrollments = "DELETE FROM enrollment WHERE group_id=" + id;
        // Then delete the group
        String deleteGroup = "DELETE FROM groups WHERE id=" + id;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteEnrollments);
            int rowsAffected = stmt.executeUpdate(deleteGroup);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting group: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reassignGroups(int fromTeacherId, int toTeacherId) {
        String sql = "UPDATE groups SET teacher_id=" + toTeacherId + " WHERE teacher_id=" + fromTeacherId;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error reassigning groups: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> getStudentsInGroup(int groupId) {
        List<User> students = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.lastname, p.email, p.password, p.role " +
                    "FROM people p " +
                    "INNER JOIN enrollment e ON p.id = e.student_id " +
                    "WHERE e.group_id=" + groupId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students in group: " + e.getMessage());
        }

        return students;
    }

    @Override
    public boolean enrollStudent(int groupId, int studentId) {
        // Check if already enrolled
        if (isStudentEnrolled(groupId, studentId)) {
            System.out.println("Student already enrolled in group");
            return false;
        }

        try {
            InsertIntoDatabase.insertEnrollment(conn, groupId, studentId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeStudent(int groupId, int studentId) {
        String sql = "DELETE FROM enrollment WHERE group_id=" + groupId +
                    " AND student_id=" + studentId;

        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing student from group: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Group> getGroupsByStudent(int studentId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT g.id, g.name, g.teacher_id " +
                    "FROM groups g " +
                    "INNER JOIN enrollment e ON g.id = e.group_id " +
                    "WHERE e.student_id=" + studentId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                groups.add(extractGroupFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting groups by student: " + e.getMessage());
        }

        return groups;
    }

    @Override
    public boolean isStudentEnrolled(int groupId, int studentId) {
        String sql = "SELECT COUNT(*) as count FROM enrollment " +
                    "WHERE group_id=" + groupId + " AND student_id=" + studentId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking enrollment: " + e.getMessage());
        }

        return false;
    }

    // Helper method to extract Group from ResultSet
    private Group extractGroupFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int teacherId = rs.getInt("teacher_id");

        return new Group(id, name, teacherId);
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
        if (roleStr.equalsIgnoreCase("admin")) {
            role = User.Role.ADMIN;
        } else if (roleStr.equalsIgnoreCase("teacher")) {
            role = User.Role.TEACHER;
        } else {
            role = User.Role.STUDENT;
        }

        return new User(id, email, password, name, lastname, role);
    }
}
