package com.example.database;
import com.example.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static com.example.model.User.Role.*;

public class DatabaseManager {
    // ======= CONNECTING DATABASE ======
    // 1. The Connection String
    private static final String URL = "jdbc:sqlite:group5Quiz.db"; // "jdbc:sqlite:" is the protocol
    public static void main(String[] args) {
        System.out.println("Connecting to database...");
        // 2. Establish Connection
        // DriverManager asks the driver to open a link to the URL
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                System.out.println("Connected to SQLite successfully!");
                // We will call our helper methods here later

                // Create tables
                createPeopleTable(conn);
                createGroupTable(conn);
                createEnrollmentTable(conn);
                createQuizTable(conn);
                createMCQTable(conn);
                createQuizQuestionTable(conn);
                createScoresTable(conn);
                createMcqStudentAnswerTable(conn);


            }
        } catch (SQLException e) {
            // If something goes wrong (like the driver is missing), this prints the error.
            System.out.println("Error: " + e.getMessage());
        }
    }




    // ========= CREATING TABLES ==========
    // People table
    private static void createPeopleTable(Connection conn) throws SQLException {
        // SQL to create a table named 'people' with 6 columns
        String sql = "CREATE TABLE IF NOT EXISTS people (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " lastname text NOT NULL,\n"
                + " email text NOT NULL,\n"
                + " password text NOT NULL,\n"
                + " role text NOT NULL\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'people' is ready.");
        }
    }

    // Group table
    private static void createGroupTable(Connection conn) throws SQLException {
        // SQL to create a table named 'groups' with 3 columns
        String sql = "CREATE TABLE IF NOT EXISTS groups (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " teacher_id integer NOT NULL,\n"
                + " FOREIGN KEY (teacher_id) REFERENCES people(id)\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'groups' is ready.");
        }
    }

    // Enrollment(connection between group and students)
    private static void createEnrollmentTable(Connection conn) throws SQLException {
        // SQL to create a table named 'enrollment' with 2 columns
        String sql = "CREATE TABLE IF NOT EXISTS enrollment (\n"
                + " group_id integer NOT NULL,\n"
                + " student_id integer NOT NULL,\n"
                + " FOREIGN KEY (group_id) REFERENCES groups(id),\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id)\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'enrollment' is ready.");
        }
    }

    // Quiz
    private static void createQuizTable(Connection conn) throws SQLException {
        // SQL to create a table named 'quiz' with 4 columns
        String sql = "CREATE TABLE IF NOT EXISTS quiz (\n"
                + " id integer PRIMARY KEY,\n"
                + " quiz_name text NOT NULL,\n"
                + " description text NOT NULL,\n"
                + " group_id integer NOT NULL,\n"
                + " FOREIGN KEY (group_id) REFERENCES groups(id)\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'quiz' is ready.");
        }
    }

    // Multiple Choice Question
    private static void createMCQTable(Connection conn) throws SQLException {
        // SQL to create a table named 'mcq' with 8 columns
        String sql = "CREATE TABLE IF NOT EXISTS mcq (\n"
                + " id integer PRIMARY KEY,\n"
                + " question text NOT NULL,\n"
                + " optionA text NOT NULL,\n"
                + " optionB text NOT NULL,\n"
                + " optionC text NOT NULL,\n"
                + " optionD text NOT NULL,\n"
                + " correct_option char NOT NULL,\n"
                + " assigned_score integer NOT NULL\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'mcq' is ready.");
        }
    }

    // QuizQuestion
    private static void createQuizQuestionTable(Connection conn) throws SQLException {
        // SQL to create a table named 'quizQuestion' with 2 columns
        String sql = "CREATE TABLE IF NOT EXISTS quizQuestion (\n"
                + " quiz_id integer NOT NULL,\n"
                + " question_id integer NOT NULL,\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id),\n"
                + " FOREIGN KEY (question_id) REFERENCES mcq(id)\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'quizQuestion' is ready.");
        }
    }

    // Scores
    private static void createScoresTable(Connection conn) throws SQLException {
        // SQL to create a table named 'scores' with 4 columns
        String sql = "CREATE TABLE IF NOT EXISTS scores (\n"
                + " quiz_id integer NOT NULL,\n"
                + " student_id integer NOT NULL,\n"
                + " attempt integer NOT NULL,\n"
                + " score integer NOT NULL,\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id),\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id)\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'scores' is ready.");
        }
    }

    // MCQ Student Answer
    private static void createMcqStudentAnswerTable(Connection conn) throws SQLException {
        // SQL to create a table named 'mcqStudentAnswer' with 8 columns
        String sql = "CREATE TABLE IF NOT EXISTS mcqStudentAnswer (\n"
                + " id integer PRIMARY KEY,\n"
                + " question_id integer NOT NULL,\n"
                + " student_id integer NOT NULL,\n"
                + " attempt integer NOT NULL,\n"
                + " selected_option char NOT NULL,\n"
                + " is_correct boolean NOT NULL,\n"
                + " score int NOT NULL,\n"
                + " date date NOT NULL,\n"
                + " FOREIGN KEY (question_id) REFERENCES mcq(id),\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id)\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'mcqStudentAnswer' is ready.");
        }
    }




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
   private static Map<Integer, User> readAllUserData(Connection conn) throws SQLException {
       String sql = "SELECT id, name, lastname, email, password, role FROM people";
       Map<Integer,User> users = new HashMap<>();
       try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) { // executeQuery returns data
           // Loop through the result set. rs.next() returns false when there are no more rows.
           while (rs.next()) {
               int id = rs.getInt("id");
               String name = rs.getString("name");
               String lastname = rs.getString("lastname");
               String email = rs.getString("email");
               String password = rs.getString("password");
               String role = rs.getString("role");
               User.Role user_role;
               if (role.equalsIgnoreCase("admin") ){
                   user_role = ADMIN;
               } else if (role.equalsIgnoreCase("teacher")) {
                   user_role = TEACHER;
               }else{
                   user_role = STUDENT;
               }

               users.put(id,new User(id,email,password,name,lastname,user_role));
           }
       }
       return users;
   }

}


