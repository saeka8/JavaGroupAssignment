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
    // Triggered by: Admin creating a new user
    // Returns: The ID of the newly created user
    public static int insertPeople(Connection conn, String name, String lastName, String email, String password, String role) throws SQLException {
        // WARNING: In real apps, use 'PreparedStatement' to prevent SQL Injection.
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO people(name, lastname,email,password,role) VALUES('" + name +
                "', '" + lastName + "', '" + email + "', '" + password + "', '" + role + "')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted user: " + name + " " + lastName + " with role " + role);

            // Get the generated user ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // If auto-generated keys not available, retrieve by email
            return RetrieveFromDatabase.getUserIdByEmail(conn, email);
        }
    }

    // When teacher creates a group
    // Triggered by: Teacher creating a group
    // Required logic: Look for existing teacher and retrieve teacherId
    public static void insertGroup(Connection conn, String groupName, String teacherEmail) throws SQLException {
        // Retrieve teacher ID first
        int teacherId = RetrieveFromDatabase.getTeacherId(conn, teacherEmail);

        // WARNING: In real apps, use 'PreparedStatement' to prevent SQL Injection.
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO groups(name,teacher_id) VALUES('" + groupName +
                "', " + teacherId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted group named: " + groupName + " with teacher id " + teacherId);
        }
    }

    // Overloaded method for direct teacher ID usage
    public static void insertGroup(Connection conn, String groupName, int teacherId) throws SQLException {
        // WARNING: In real apps, use 'PreparedStatement' to prevent SQL Injection.
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO groups(name,teacher_id) VALUES('" + groupName +
                "', " + teacherId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted group named: " + groupName + " with teacher id " + teacherId);
        }
    }

    // When student is assigned to the group
    // Triggered by: Student being assigned to a group
    // Required logic: Look for existing group ID and student ID
    public static void insertEnrollment(Connection conn, String groupName, String studentEmail) throws SQLException {
        // Retrieve group ID and student ID
        int groupId = RetrieveFromDatabase.getGroupId(conn, groupName);
        int studentId = RetrieveFromDatabase.getStudentId(conn, studentEmail);

        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO enrollment(group_id,student_id) VALUES(" + groupId +
                ", " + studentId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Enrolled student id " + studentId + " into group id " + groupId);
        }
    }

    // Overloaded method for direct IDs
    public static void insertEnrollment(Connection conn, int groupId, int studentId) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO enrollment(group_id,student_id) VALUES(" + groupId +
                ", " + studentId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Enrolled student id " + studentId + " into group id " + groupId);
        }
    }

    // When quiz pack is created
    // Triggered by: Quiz pack being created
    // Required logic: Look for existing group ID
    // Returns: The ID of the newly created quiz
    public static int insertQuiz(Connection conn, String quizName, String description, String groupName) throws SQLException {
        // Retrieve group ID
        int groupId = RetrieveFromDatabase.getGroupId(conn, groupName);

        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO quiz(quiz_name,description,group_id) VALUES('" + quizName +
                "', '" + description + "', " + groupId +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted quiz named " + quizName);

            // Get the generated quiz ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // If auto-generated keys not available, retrieve by name
            return RetrieveFromDatabase.getQuizId(conn, quizName);
        }
    }

    // Overloaded method for direct group ID
    public static int insertQuiz(Connection conn, String quizName, String description, int groupId) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO quiz(quiz_name,description,group_id) VALUES('" + quizName +
                "', '" + description + "', " + groupId +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted quiz named " + quizName);

            // Get the generated quiz ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // If auto-generated keys not available, retrieve by name
            return RetrieveFromDatabase.getQuizId(conn, quizName);
        }
    }

    // When multiple choice question is created
    // Triggered by: Question being created
    // Returns: The ID of the newly created question
    public static int insertMcq(Connection conn, String question, String optionA, String optionB, String optionC, String optionD, char correctOption, int assignedScore) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO mcq(question,optionA,optionB,optionC,optionD,correct_option,assigned_score) VALUES('" + question +
                "', '" + optionA + "', '"  + optionB + "', '" + optionC + "', '" + optionD + "', '" + correctOption + "', " + assignedScore +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted new multiple choice question");

            // Get the generated question ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // If auto-generated keys not available, retrieve by question text
            return RetrieveFromDatabase.getQuestionIdByText(conn, question);
        }
    }

    // When question is created in the quiz
    // Triggered by: Question being created and added to quiz (after insertMcq() and insertQuiz())
    // Required logic: Get quiz ID and question ID to create connection
    // Note: This should be called after insertMcq() and insertQuiz() have been executed
    public static void insertQuizQuestion(Connection conn, int quizId, int questionId) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO quizQuestion(quiz_id,question_id) VALUES(" + quizId +
                ", " + questionId + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Question id " + questionId + " added to Quiz id " + quizId);
        }
    }

    // When student submits quiz and score needs to be recorded
    // Triggered by: Student submitting quiz answers
    // Required logic: Look for existing quiz ID and student ID, track attempt number, calculate score
    public static void insertScore(Connection conn, String quizName, String studentEmail, int score) throws SQLException {
        // Retrieve quiz ID and student ID
        int quizId = RetrieveFromDatabase.getQuizId(conn, quizName);
        int studentId = RetrieveFromDatabase.getStudentId(conn, studentEmail);

        // Get the next attempt number
        int attempt = RetrieveFromDatabase.getNextAttemptNumber(conn, quizId, studentId);

        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO scores(quiz_id,student_id, attempt, score) VALUES(" + quizId +
                ", " + studentId + ", " + attempt + ", " + score +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted score " + score + " for attempt " + attempt + " on quiz id " + quizId + " for student id " + studentId);
        }
    }

    // Overloaded method for direct IDs and attempt number
    public static void insertScore(Connection conn, int quizId, int studentId, int attempt, int score) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO scores(quiz_id,student_id, attempt, score) VALUES(" + quizId +
                ", " + studentId + ", " + attempt + ", " + score +  ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Inserted score " + score + " for attempt " + attempt + " on quiz id " + quizId + " for student id " + studentId);
        }
    }

    // When student submits an answer to a question
    // Triggered by: Student answering a question
    // Required logic: Look for existing question ID and student ID, track attempt number,
    //                 check if answer is correct, calculate score based on correctness
    public static void insertStudentAnswer(Connection conn, int questionId, String studentEmail, int attempt, char selectedOption) throws SQLException {
        // Retrieve student ID
        int studentId = RetrieveFromDatabase.getStudentId(conn, studentEmail);

        // Get correct option for the question
        char correctOption = RetrieveFromDatabase.getCorrectOption(conn, questionId);

        // Check if answer is correct
        boolean isCorrect = (selectedOption == correctOption);

        // Get assigned score for the question
        int assignedScore = RetrieveFromDatabase.getAssignedScore(conn, questionId);

        // Calculate score: full points if correct, 0 if incorrect
        int score = isCorrect ? assignedScore : 0;

        // Get current date
        LocalDate date = LocalDate.now();

        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO mcqStudentAnswer(question_id,student_id, attempt, selected_option, is_correct, score, date) VALUES(" + questionId +
                ", " + studentId + ", " + attempt + ", '" + selectedOption + "', " + isCorrect + ", " + score + ", '" + date + "')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Student answer '" + selectedOption + "' inserted. Correct: " + isCorrect + ", Score: " + score);
        }
    }

    // Overloaded method for direct student ID
    public static void insertStudentAnswer(Connection conn, int questionId, int studentId, int attempt, char selectedOption) throws SQLException {
        // Get correct option for the question
        char correctOption = RetrieveFromDatabase.getCorrectOption(conn, questionId);

        // Check if answer is correct
        boolean isCorrect = (selectedOption == correctOption);

        // Get assigned score for the question
        int assignedScore = RetrieveFromDatabase.getAssignedScore(conn, questionId);

        // Calculate score: full points if correct, 0 if incorrect
        int score = isCorrect ? assignedScore : 0;

        // Get current date
        LocalDate date = LocalDate.now();

        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO mcqStudentAnswer(question_id,student_id, attempt, selected_option, is_correct, score, date) VALUES(" + questionId +
                ", " + studentId + ", " + attempt + ", '" + selectedOption + "', " + isCorrect + ", " + score + ", '" + date + "')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Student answer '" + selectedOption + "' inserted. Correct: " + isCorrect + ", Score: " + score);
        }
    }

    // Original method signature kept for backward compatibility
    public static void insertStudentAnswer(Connection conn, int questionId, int studentId, int attempt, char selectedOption, boolean isCorrect, int score, LocalDate date) throws SQLException {
        // We use simple string concatenation here for learning purposes only.
        String sql = "INSERT INTO mcqStudentAnswer(question_id,student_id, attempt, selected_option, is_correct, score, date) VALUES(" + questionId +
                ", " + studentId + ", " + attempt + ", '" + selectedOption + "', " + isCorrect + ", " + score + ", '" + date + "')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Student answer '" + selectedOption + "' inserted. Correct: " + isCorrect + ", Score: " + score);
        }
    }
}
