package com.example.database;

import java.sql.*;

public class DatabaseManager {
    // ======= CONNECTING DATABASE ======
    // 1. The Connection String
    private static final String URL = "jdbc:sqlite:group5Quiz.db"; // "jdbc:sqlite:" is the protocol

    /**
     * Get a connection to the database.
     * Creates the database file if it doesn't exist.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initialize the database - create all tables if they don't exist.
     */
    public static void initializeDatabase() {
        System.out.println("Connecting to database...");
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to SQLite successfully!");
                createPeopleTable(conn);
                createQuizTable(conn);
                createQuestionTable(conn);
                createQuizAssignmentTable(conn);
                createQuizAttemptTable(conn);
                createStudentAnswerTable(conn);
                System.out.println("All tables ready!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }

    // ========= CREATING TABLES ==========

    // People/User table
    private static void createPeopleTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS people (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " first_name TEXT NOT NULL,\n"
                + " last_name TEXT NOT NULL,\n"
                + " email TEXT NOT NULL UNIQUE,\n"
                + " password TEXT NOT NULL,\n"
                + " role TEXT NOT NULL,\n"
                + " created_at DATETIME DEFAULT CURRENT_TIMESTAMP\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'people' is ready.");
        }
    }

    // Quiz table
    private static void createQuizTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS quiz (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " title TEXT NOT NULL,\n"
                + " description TEXT,\n"
                + " teacher_id INTEGER NOT NULL,\n"
                + " created_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
                + " is_active INTEGER DEFAULT 1,\n"
                + " FOREIGN KEY (teacher_id) REFERENCES people(id)\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'quiz' is ready.");
        }
    }

    // Question table (MCQ)
    private static void createQuestionTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS question (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " quiz_id INTEGER NOT NULL,\n"
                + " question_text TEXT NOT NULL,\n"
                + " option_a TEXT NOT NULL,\n"
                + " option_b TEXT NOT NULL,\n"
                + " option_c TEXT NOT NULL,\n"
                + " option_d TEXT NOT NULL,\n"
                + " correct_option CHAR(1) NOT NULL,\n"
                + " assigned_score INTEGER DEFAULT 1,\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'question' is ready.");
        }
    }

    // Quiz Assignment table
    private static void createQuizAssignmentTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS quiz_assignment (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " quiz_id INTEGER NOT NULL,\n"
                + " student_id INTEGER NOT NULL,\n"
                + " assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id) ON DELETE CASCADE,\n"
                + " UNIQUE(quiz_id, student_id)\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'quiz_assignment' is ready.");
        }
    }

    // Quiz Attempt table
    private static void createQuizAttemptTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS quiz_attempt (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " quiz_id INTEGER NOT NULL,\n"
                + " student_id INTEGER NOT NULL,\n"
                + " score REAL NOT NULL,\n"
                + " attempted_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id) ON DELETE CASCADE\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'quiz_attempt' is ready.");
        }
    }

    // Student Answer table
    private static void createStudentAnswerTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS student_answer (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " attempt_id INTEGER NOT NULL,\n"
                + " question_id INTEGER NOT NULL,\n"
                + " selected_option CHAR(1) NOT NULL,\n"
                + " is_correct INTEGER NOT NULL,\n"
                + " FOREIGN KEY (attempt_id) REFERENCES quiz_attempt(id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'student_answer' is ready.");
        }
    }

    // ====== INSERT HELPERS =======

    public static int insertUser(String firstName, String lastName, String email, String password, String role) {
        String sql = "INSERT INTO people(first_name, last_name, email, password, role) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, role);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
        return -1;
    }

    public static int insertQuiz(String title, String description, int teacherId) {
        String sql = "INSERT INTO quiz(title, description, teacher_id) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, teacherId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error inserting quiz: " + e.getMessage());
        }
        return -1;
    }

    public static int insertQuestion(int quizId, String questionText, String optionA, String optionB, 
                                     String optionC, String optionD, char correctOption, int assignedScore) {
        String sql = "INSERT INTO question(quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option, assigned_score) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, quizId);
            pstmt.setString(2, questionText);
            pstmt.setString(3, optionA);
            pstmt.setString(4, optionB);
            pstmt.setString(5, optionC);
            pstmt.setString(6, optionD);
            pstmt.setString(7, String.valueOf(correctOption));
            pstmt.setInt(8, assignedScore);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error inserting question: " + e.getMessage());
        }
        return -1;
    }

    public static boolean assignQuizToStudent(int quizId, int studentId) {
        String sql = "INSERT OR IGNORE INTO quiz_assignment(quiz_id, student_id) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error assigning quiz: " + e.getMessage());
        }
        return false;
    }

    public static int insertQuizAttempt(int quizId, int studentId, double score) {
        String sql = "INSERT INTO quiz_attempt(quiz_id, student_id, score) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, quizId);
            pstmt.setInt(2, studentId);
            pstmt.setDouble(3, score);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error inserting attempt: " + e.getMessage());
        }
        return -1;
    }

    public static boolean insertStudentAnswer(int attemptId, int questionId, char selectedOption, boolean isCorrect) {
        String sql = "INSERT INTO student_answer(attempt_id, question_id, selected_option, is_correct) VALUES(?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attemptId);
            pstmt.setInt(2, questionId);
            pstmt.setString(3, String.valueOf(selectedOption));
            pstmt.setInt(4, isCorrect ? 1 : 0);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting answer: " + e.getMessage());
        }
        return false;
    }
}
