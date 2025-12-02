package com.example.database;

import com.example.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static com.example.model.User.Role.*;

public class InsertIntoDatabase {
    // ====== Insert data =======
    // When admin create user
    private static void insertPeople(Connection conn, String name, String lastName, String email, String password, String role) throws SQLException {
        // WARNING: In real apps, use 'PreparedStatement' to prevent SQL Injection.
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO people(name, lastname,email,password,role) VALUES('" + name +
                "', '" + lastName + "', '" + email + "', '" + password + "', '" + role + "')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted user: " + name + " " + lastName);
        }
    }

    // When admin create groups
    private static void insertGroup(Connection conn, String groupName,int teacherId) throws SQLException {
        // WARNING: In real apps, use 'PreparedStatement' to prevent SQL Injection.
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO group(name,teacher_id) VALUES('" + groupName +
                "', " + teacherId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted group named: " + groupName + " with teacher id " + teacherId);
        }
    }

    // When student is assigned into the group
    private static void insertEnrollment(Connection conn, int groupId, int studentId) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO enrollment(group_id,student_id) VALUES(" + groupId +
                ", " + studentId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("enrolled student id " + studentId + "into group id" + groupId);
        }
    }

    // When quiz pack is created
    private static void insertQuiz(Connection conn, String quizName, String description, int groupId) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO quiz(quiz_name,description,group_id) VALUES('" + quizName +
                "', '" + description + "', " + groupId +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("inserted quiz named " + quizName);
        }
    }

    // When multiple choice question is created
    private static void insertMcq(Connection conn, String question, String optionA, String optionB, String optionC, String optionD, char correctOption, int assignedScore) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO mcq(question,optionA,optionB,optionC,optionD,correct_option,assigned_score) VALUES('" + question +
                "', '" + optionA + "', '"  + optionB + "', '" + optionC + "', '" + optionD + "', '" + correctOption + "', " + assignedScore +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted new multiple choice question");
        }
    }

    // When question added in the quiz
    private static void insertQuizQuestion(Connection conn, int quizId, int questionId) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO quizQuestion(quiz_id,question_id) VALUES(" + quizId +
                ", " + questionId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Question id " + questionId + " added in the Quiz id " + quizId);
        }
    }

    // When logic calculated the score
    private static void insertScore(Connection conn, int quizId, int studentId, int attempt, int score) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO scores(quiz_id,student_id, attempt, score) VALUES(" + quizId +
                ", " + studentId + ", " + attempt + ", " + score +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted score " + score + " for " + attempt + " times attempt for quiz id " + quizId + " with student id " + studentId);
        }
    }

    // When student answer question and want to save that answer
    private static void insertStudentAnswer(Connection conn, int questionId, int studentId, int attempt, char selectedOption, boolean isCorrect, int score, LocalDate date) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO mcqStudentAnswer(question_id,student_id, attempt, selected_option, is_correct, score, date) VALUES(" + questionId +
                ", " + studentId + ", " + attempt + ", '" + selectedOption + "', " + isCorrect + ", " + score + ", " + date + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("StudentAnswer " + selectedOption + "is inserted");
        }
    }



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
}
