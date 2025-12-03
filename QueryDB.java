import java.sql.*;

public class QueryDB {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:group5Quiz.db");
        Statement stmt = conn.createStatement();

        System.out.println("\n=== PEOPLE ===");
        ResultSet rs = stmt.executeQuery("SELECT * FROM people ORDER BY id");
        while(rs.next()) {
            System.out.println(rs.getInt("id") + " | " +
                rs.getString("name") + " " + rs.getString("lastname") + " | " +
                rs.getString("email") + " | " + rs.getString("role"));
        }

        System.out.println("\n=== GROUPS ===");
        rs = stmt.executeQuery("SELECT g.id, g.name, p.name || ' ' || p.lastname as teacher FROM groups g JOIN people p ON g.teacher_id = p.id ORDER BY g.id");
        while(rs.next()) {
            System.out.println(rs.getInt("id") + " | " + rs.getString("name") + " | Teacher: " + rs.getString("teacher"));
        }

        System.out.println("\n=== ENROLLMENT ===");
        rs = stmt.executeQuery("SELECT g.name as group_name, p.name || ' ' || p.lastname as student FROM enrollment e JOIN groups g ON e.group_id = g.id JOIN people p ON e.student_id = p.id ORDER BY g.id, p.id");
        while(rs.next()) {
            System.out.println(rs.getString("group_name") + " -> " + rs.getString("student"));
        }

        System.out.println("\n=== QUIZZES ===");
        rs = stmt.executeQuery("SELECT q.id, q.quiz_name, q.description, g.name as group_name FROM quiz q JOIN groups g ON q.group_id = g.id ORDER BY q.id");
        while(rs.next()) {
            System.out.println(rs.getInt("id") + " | " + rs.getString("quiz_name") + " | Group: " + rs.getString("group_name"));
        }

        System.out.println("\n=== MCQ (Questions) ===");
        rs = stmt.executeQuery("SELECT id, question, correct_option, assigned_score FROM mcq ORDER BY id");
        while(rs.next()) {
            System.out.println(rs.getInt("id") + " | Score: " + rs.getInt("assigned_score") + " | Correct: " + rs.getString("correct_option") + " | " + rs.getString("question"));
        }

        System.out.println("\n=== QUIZ QUESTIONS (Linkage) ===");
        rs = stmt.executeQuery("SELECT qq.quiz_id, q.quiz_name, qq.question_id FROM quizQuestion qq JOIN quiz q ON qq.quiz_id = q.id ORDER BY qq.quiz_id, qq.question_id");
        while(rs.next()) {
            System.out.println("Quiz " + rs.getInt("quiz_id") + " (" + rs.getString("quiz_name") + ") -> Question " + rs.getInt("question_id"));
        }

        System.out.println("\n=== SCORES ===");
        rs = stmt.executeQuery("SELECT COUNT(*) as count FROM scores");
        rs.next();
        System.out.println("Total score records: " + rs.getInt("count"));

        stmt.close();
        conn.close();
    }
}
