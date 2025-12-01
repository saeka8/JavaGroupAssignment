package src.database;
import java.sql.*;

public class DatabaseManager {
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

    private static void createNewTable(Connection conn) throws SQLException {
        // SQL to create a table named 'students' with 3 columns
        String sql = "CREATE TABLE IF NOT EXISTS students (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " grade integer\n"
                + ");";
        // Create a Statement object to carry the SQL
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL command
            stmt.execute(sql);
            System.out.println("Table 'students' is ready.");
        }
    }


}
