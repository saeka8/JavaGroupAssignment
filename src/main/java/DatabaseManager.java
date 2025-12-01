package src.database;
import java.sql.*;

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
                + " role text NOT NULL,\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'people' is ready.");
        }
    }

    // Group table
    private static void groupTable(Connection conn) throws SQLException {
        // SQL to create a table named 'groups' with 3 columns
        String sql = "CREATE TABLE IF NOT EXISTS groups (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " FOREIGN KEY (teacher_id) REFERENCES people(id),\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'groups' is ready.");
        }
    }

    // Enrollment(connection between group and students)
    private static void enrollmentTable(Connection conn) throws SQLException {
        // SQL to create a table named 'enrollment' with 2 columns
        String sql = "CREATE TABLE IF NOT EXISTS enrollment (\n"
                + " FOREIGN KEY (group_id) REFERENCES groups(id),\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id),\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'enrollment' is ready.");
        }
    }

    // Quiz
    private static void quizTable(Connection conn) throws SQLException {
        // SQL to create a table named 'quiz' with 4 columns
        String sql = "CREATE TABLE IF NOT EXISTS enrollment (\n"
                + " id integer PRIMARY KEY,\n"
                + " quiz_name text NOT NULL,\n"
                + " description text NOT NULL,\n"
                + " FOREIGN KEY (group_id) REFERENCES groups(id),\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'quiz' is ready.");
        }
    }

    // Multiple Choice Question
    private static void MCQTable(Connection conn) throws SQLException {
        // SQL to create a table named 'mcq' with 8 columns
        String sql = "CREATE TABLE IF NOT EXISTS mcq (\n"
                + " id integer PRIMARY KEY,\n"
                + " question text NOT NULL,\n"
                + " optionA text NOT NULL,\n"
                + " optionB text NOT NULL,\n"
                + " optionC text NOT NULL,\n"
                + " optionD text NOT NULL,\n"
                + " correct_option char NOT NULL,\n"
                + " assigned_score integer NOT NULL,\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'mcq' is ready.");
        }
    }

    // QuizQuestion
    private static void quizQuestionTable(Connection conn) throws SQLException {
        // SQL to create a table named 'quizQuestion' with 2 columns
        String sql = "CREATE TABLE IF NOT EXISTS quizQuestion (\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id),\n"
                + " FOREIGN KEY (question_id) REFERENCES mcq(id),\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'quizQuestion' is ready.");
        }
    }

    // Scores
    private static void scoresTable(Connection conn) throws SQLException {
        // SQL to create a table named 'scores' with 4 columns
        String sql = "CREATE TABLE IF NOT EXISTS scores (\n"
                + " FOREIGN KEY (quiz_id) REFERENCES quiz(id),\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id),\n"
                + " attempt integer NOT NULL,\n"
                + " score integer NOT NULL,\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'scores' is ready.");
        }
    }

    // MCQ Student Answer
    private static void mcqStudentAnswerTable(Connection conn) throws SQLException {
        // SQL to create a table named 'mcqStudentAnswer' with 8 columns
        String sql = "CREATE TABLE IF NOT EXISTS mcqStudentAnswer (\n"
                + " id integer PRIMARY KEY,\n"
                + " FOREIGN KEY (question_id) REFERENCES mcq(id),\n"
                + " FOREIGN KEY (student_id) REFERENCES people(id),\n"
                + " attempt integer NOT NULL,\n"
                + " selected_option char NOT NULL,\n"
                + " is_correct boolean NOT NULL,\n"
                + " score int NOT NULL,\n"
                + " date date NOT NULL,\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'mcqStudentAnswer' is ready.");
        }
    }
}
