package com.example.database;

import com.example.model.User;
import com.example.quizlogic.QuizAttempt;

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

    // Retrieve teacher ID by email
    public static int getTeacherId(Connection conn, String teacherEmail) throws SQLException {
        String sql = "SELECT id FROM people WHERE email='" + teacherEmail + "' AND role='teacher'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Teacher not found with email: " + teacherEmail);
    }

    // Retrieve group ID by group name
    public static int getGroupId(Connection conn, String groupName) throws SQLException {
        String sql = "SELECT id FROM groups WHERE name='" + groupName + "'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Group not found with name: " + groupName);
    }

    // Retrieve student ID by email
    public static int getStudentId(Connection conn, String studentEmail) throws SQLException {
        String sql = "SELECT id FROM people WHERE email='" + studentEmail + "' AND role='student'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Student not found with email: " + studentEmail);
    }

    // Retrieve quiz ID by quiz name
    public static int getQuizId(Connection conn, String quizName) throws SQLException {
        String sql = "SELECT id FROM quiz WHERE quiz_name='" + quizName + "'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Quiz not found with name: " + quizName);
    }

    // Retrieve question ID by question text (last inserted)
    public static int getQuestionIdByText(Connection conn, String questionText) throws SQLException {
        String sql = "SELECT id FROM mcq WHERE question='" + questionText + "' ORDER BY id DESC LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Question not found");
    }

    // Get the next attempt number for a student on a specific quiz
    public static int getNextAttemptNumber(Connection conn, int quizId, int studentId) throws SQLException {
        String sql = "SELECT MAX(attempt) as max_attempt FROM scores WHERE quiz_id=" + quizId + " AND student_id=" + studentId;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxAttempt = rs.getInt("max_attempt");
                return maxAttempt + 1;
            }
        }
        return 1; // First attempt
    }

    // Get correct option for a question
    public static char getCorrectOption(Connection conn, int questionId) throws SQLException {
        String sql = "SELECT correct_option FROM mcq WHERE id=" + questionId;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("correct_option").charAt(0);
            }
        }
        throw new SQLException("Question not found with id: " + questionId);
    }

    // Get assigned score for a question
    public static int getAssignedScore(Connection conn, int questionId) throws SQLException {
        String sql = "SELECT assigned_score FROM mcq WHERE id=" + questionId;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("assigned_score");
            }
        }
        throw new SQLException("Question not found with id: " + questionId);
    }

    // Get student's scores lists by QuizId
    public static Map<Integer, QuizAttempt> getScores(Connection conn, int quizId, int studentId) throws SQLException {
        String sql = "SELECT attempt, score FROM scores WHERE quiz_id = " + quizId + " AND student_id = " + studentId;
        Map<Integer, QuizAttempt> studentScores = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int attempt = rs.getInt("attempt");
                int score = rs.getInt("score");

                // Use attempt number as key
                studentScores.put(attempt, new QuizAttempt(quizId, studentId, attempt, score));
            }
        }
        if (studentScores.isEmpty()) {
            throw new SQLException("No quiz attempts found for quiz_id: " + quizId + " and student_id: " + studentId);
        }
        return studentScores;
    }

    
    // Retrieve user ID by email
    public static int getUserIdByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT id FROM people WHERE email='" + email + "'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("User not found with email: " + email);
    }
}
