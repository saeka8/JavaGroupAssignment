package com.example.util;

import com.example.database.DatabaseManager;
import java.sql.*;

public class QueryCurrentDB {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseManager.connectWithDatabase();
            if (conn == null) {
                System.err.println("Failed to connect to database!");
                return;
            }

            Statement stmt = conn.createStatement();

            System.out.println("\n=== PEOPLE (18 total) ===");
            ResultSet rs = stmt.executeQuery("SELECT * FROM people ORDER BY id");
            while(rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                    rs.getString("name") + " " + rs.getString("lastname") + " | " +
                    rs.getString("email") + " | " + rs.getString("role"));
            }

            System.out.println("\n=== GROUPS (8 total) ===");
            rs = stmt.executeQuery("SELECT g.id, g.name, p.name || ' ' || p.lastname as teacher FROM groups g JOIN people p ON g.teacher_id = p.id ORDER BY g.id");
            while(rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("name") + " | Teacher: " + rs.getString("teacher"));
            }

            System.out.println("\n=== ENROLLMENT (22 enrollments) ===");
            rs = stmt.executeQuery("SELECT g.name as group_name, p.name || ' ' || p.lastname as student FROM enrollment e JOIN groups g ON e.group_id = g.id JOIN people p ON e.student_id = p.id ORDER BY g.id, p.id");
            while(rs.next()) {
                System.out.println(rs.getString("group_name") + " -> " + rs.getString("student"));
            }

            System.out.println("\n=== QUIZZES (12 total) ===");
            rs = stmt.executeQuery("SELECT q.id, q.quiz_name, q.description, g.name as group_name, p.name || ' ' || p.lastname as teacher FROM quiz q JOIN groups g ON q.group_id = g.id JOIN people p ON g.teacher_id = p.id ORDER BY q.id");
            while(rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("quiz_name") +
                    " | Group: " + rs.getString("group_name") +
                    " | Teacher: " + rs.getString("teacher"));
            }

            System.out.println("\n=== MCQ - Questions (18 total) ===");
            rs = stmt.executeQuery("SELECT id, SUBSTR(question, 1, 50) as q, correct_option, assigned_score FROM mcq ORDER BY id");
            while(rs.next()) {
                System.out.println(rs.getInt("id") + " | Score: " + rs.getInt("assigned_score") +
                    " | Correct: " + rs.getString("correct_option") +
                    " | " + rs.getString("q") + "...");
            }

            System.out.println("\n=== QUIZ-QUESTION LINKAGE (18 links) ===");
            rs = stmt.executeQuery("SELECT qq.quiz_id, q.quiz_name, qq.question_id FROM quizQuestion qq JOIN quiz q ON qq.quiz_id = q.id ORDER BY qq.quiz_id, qq.question_id");
            while(rs.next()) {
                System.out.println("Quiz " + rs.getInt("quiz_id") + " (" +
                    rs.getString("quiz_name") + ") -> Question " + rs.getInt("question_id"));
            }

            System.out.println("\n=== SCORES (13 records) ===");
            rs = stmt.executeQuery("SELECT s.quiz_id, q.quiz_name, p.name || ' ' || p.lastname as student, s.attempt, s.score FROM scores s JOIN quiz q ON s.quiz_id = q.id JOIN people p ON s.student_id = p.id ORDER BY s.quiz_id, s.student_id, s.attempt");
            while(rs.next()) {
                System.out.println("Quiz " + rs.getInt("quiz_id") + " (" + rs.getString("quiz_name") +
                    ") | " + rs.getString("student") +
                    " | Attempt " + rs.getInt("attempt") +
                    " | Score: " + rs.getInt("score"));
            }

            stmt.close();
            conn.close();

            System.out.println("\n✅ Database query complete!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
