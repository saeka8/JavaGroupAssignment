package com.example.dev;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class to populate the database with the EXACT data structure
 * that exists in the current database, but with corrected 1-point scoring.
 *
 * Run this after deleting group5Quiz.db to recreate the database with correct
 * scores.
 */
public class PopulateTestData {

    public static void main(String[] args) {
        System.out.println("🔄 Starting to populate database with corrected scoring...");
        System.out.println("   (All questions now worth 1 point each)\n");

        try {
            Connection conn = DatabaseManager.connectWithDatabase();
            if (conn == null) {
                System.err.println("❌ Failed to connect to database!");
                return;
            }

            // Create all tables first
            DatabaseManager.createAllTables(conn);

            System.out.println("=== Creating 18 Users ===");
            createUsers(conn);

            System.out.println("\n=== Creating 8 Groups ===");
            createGroups(conn);

            System.out.println("\n=== Creating 22 Student Enrollments ===");
            enrollStudents(conn);

            System.out.println("\n=== Creating 12 Quizzes ===");
            createQuizzes(conn);

            System.out.println("\n=== Adding 18 Questions (all 1 point each) ===");
            addQuestions(conn);

            System.out.println("\n✅ Database population complete!");
            System.out.println("\n📝 Login Credentials:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Admin:");
            System.out.println("  admin@quiz.com / admin123");
            System.out.println("\nTeachers:");
            System.out.println("  alice.teacher@quiz.com / teacher123");
            System.out.println("  saeka@gmail.com / password");
            System.out.println("  simonida@gmail.com / password");
            System.out.println("  jdoe@gmail.com / password");
            System.out.println("\nStudents:");
            System.out.println("  charlie.student@quiz.com / student123");
            System.out.println("  diana.student@quiz.com / student123");
            System.out.println("  eve.student@quiz.com / student123");
            System.out.println("  frank.student@quiz.com / student123");
            System.out.println("  grace.student@quiz.com / student123");
            System.out.println("  henry.student@quiz.com / student123");
            System.out.println("  sofia@gmail.com / password");
            System.out.println("  nathan@gmail.com / password");
            System.out.println("  silvia@gmail.com / password");
            System.out.println("  oskar / password");
            System.out.println("  paula@gmail.com / password");
            System.out.println("  sjekic@gmail.com / password");
            System.out.println("  jekic@gmail.com / password");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (SQLException e) {
            System.err.println("❌ Error populating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createUsers(Connection conn) throws SQLException {
        // 1. Admin
        InsertIntoDatabase.insertPeople(conn, "Admin", "User", "admin@quiz.com", "admin123", "admin");
        System.out.println("  ✓ Admin User");

        // 2-5. Teachers
        InsertIntoDatabase.insertPeople(conn, "Alice", "Teacher", "alice.teacher@quiz.com", "teacher123", "teacher");
        // Skip ID 3 (Bob was removed in real DB)
        InsertIntoDatabase.insertPeople(conn, "saeka", "ono", "saeka@gmail.com", "password", "teacher");
        InsertIntoDatabase.insertPeople(conn, "simonida", "jekic", "simonida@gmail.com", "password", "teacher");
        InsertIntoDatabase.insertPeople(conn, "John", "Doe", "jdoe@gmail.com", "password", "teacher");
        System.out.println("  ✓ 4 Teachers");

        // 6-19. Students
        InsertIntoDatabase.insertPeople(conn, "Charlie", "Student", "charlie.student@quiz.com", "student123",
                "student");
        InsertIntoDatabase.insertPeople(conn, "Diana", "Student", "diana.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Eve", "Student", "eve.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Frank", "Student", "frank.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Grace", "Student", "grace.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Henry", "Student", "henry.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "sofia", "avetisian", "sofia@gmail.com", "password", "student");
        InsertIntoDatabase.insertPeople(conn, "nathan", "ayoub", "nathan@gmail.com", "password", "student");
        InsertIntoDatabase.insertPeople(conn, "silvia", "lopez", "silvia@gmail.com", "password", "student");
        InsertIntoDatabase.insertPeople(conn, "oskar", "ozclok", "oskar", "password", "student");
        InsertIntoDatabase.insertPeople(conn, "paula", "martinez", "paula@gmail.com", "password", "student");
        InsertIntoDatabase.insertPeople(conn, "Simonida", "Jekic", "sjekic@gmail.com", "password", "student");
        InsertIntoDatabase.insertPeople(conn, "Simonida", "Jekic", "jekic@gmail.com", "password", "student");
        System.out.println("  ✓ 13 Students");
    }

    private static void createGroups(Connection conn) throws SQLException {
        // All groups taught by Alice (teacher_id = 2)
        InsertIntoDatabase.insertGroup(conn, "Morning Section A", 2);
        System.out.println("  ✓ Group 1: Morning Section A (Alice)");

        InsertIntoDatabase.insertGroup(conn, "Afternoon Section B", 2);
        System.out.println("  ✓ Group 2: Afternoon Section B (Alice)");

        InsertIntoDatabase.insertGroup(conn, "Advanced Programming", 2);
        System.out.println("  ✓ Group 3: Advanced Programming (Alice)");

        InsertIntoDatabase.insertGroup(conn, "Beginners Class", 2);
        System.out.println("  ✓ Group 4: Beginners Class (Alice)");

        // Skip group ID 5 (deleted in real DB)

        InsertIntoDatabase.insertGroup(conn, "GROUPB", 2);
        System.out.println("  ✓ Group 6: GROUPB (Alice)");

        InsertIntoDatabase.insertGroup(conn, "groupA", 2);
        System.out.println("  ✓ Group 7: groupA (Alice)");

        // Saeka's group (teacher_id = 10)
        InsertIntoDatabase.insertGroup(conn, "best group ever", 10);
        System.out.println("  ✓ Group 8: best group ever (saeka)");

        // John's group (teacher_id = 18)
        InsertIntoDatabase.insertGroup(conn, "Intro to programming", 18);
        System.out.println("  ✓ Group 9: Intro to programming (John)");
    }

    private static void enrollStudents(Connection conn) throws SQLException {
        // Morning Section A (Group 1) - Charlie(4), Diana(5), Eve(6)
        InsertIntoDatabase.insertEnrollment(conn, 1, 4);
        InsertIntoDatabase.insertEnrollment(conn, 1, 5);
        InsertIntoDatabase.insertEnrollment(conn, 1, 6);
        System.out.println("  ✓ Morning Section A: 3 students");

        // Afternoon Section B (Group 2) - Frank(7), Grace(8)
        InsertIntoDatabase.insertEnrollment(conn, 2, 7);
        InsertIntoDatabase.insertEnrollment(conn, 2, 8);
        System.out.println("  ✓ Afternoon Section B: 2 students");

        // Advanced Programming (Group 3) - Diana(5), Eve(6), Henry(9)
        InsertIntoDatabase.insertEnrollment(conn, 3, 5);
        InsertIntoDatabase.insertEnrollment(conn, 3, 6);
        InsertIntoDatabase.insertEnrollment(conn, 3, 9);
        System.out.println("  ✓ Advanced Programming: 3 students");

        // GROUPB (Group 6) - Frank(7), Henry(9)
        InsertIntoDatabase.insertEnrollment(conn, 6, 7);
        InsertIntoDatabase.insertEnrollment(conn, 6, 9);
        System.out.println("  ✓ GROUPB: 2 students");

        // groupA (Group 7) - Grace(8), Henry(9)
        InsertIntoDatabase.insertEnrollment(conn, 7, 8);
        InsertIntoDatabase.insertEnrollment(conn, 7, 9);
        System.out.println("  ✓ groupA: 2 students");

        // best group ever (Group 8) - Diana(5), sofia(11), nathan(13), silvia(14),
        // oskar(15), paula(16)
        InsertIntoDatabase.insertEnrollment(conn, 8, 5);
        InsertIntoDatabase.insertEnrollment(conn, 8, 11);
        InsertIntoDatabase.insertEnrollment(conn, 8, 13);
        InsertIntoDatabase.insertEnrollment(conn, 8, 14);
        InsertIntoDatabase.insertEnrollment(conn, 8, 15);
        InsertIntoDatabase.insertEnrollment(conn, 8, 16);
        System.out.println("  ✓ best group ever: 6 students");

        // Intro to programming (Group 9) - sofia(11), silvia(14), Simonida(17),
        // Simonida(19)
        InsertIntoDatabase.insertEnrollment(conn, 9, 11);
        InsertIntoDatabase.insertEnrollment(conn, 9, 14);
        InsertIntoDatabase.insertEnrollment(conn, 9, 17);
        InsertIntoDatabase.insertEnrollment(conn, 9, 19);
        System.out.println("  ✓ Intro to programming: 4 students");
    }

    private static void createQuizzes(Connection conn) throws SQLException {
        // Quiz 1 - Morning Section A (Group 1)
        InsertIntoDatabase.insertQuiz(conn, "Java Basics Quiz", "Test your knowledge of Java fundamentals", 1);
        System.out.println("  ✓ Quiz 1: Java Basics Quiz (Morning Section A)");

        // Quiz 2 - Afternoon Section B (Group 2)
        InsertIntoDatabase.insertQuiz(conn, "Object-Oriented Programming", "Understanding OOP concepts in Java", 2);
        System.out.println("  ✓ Quiz 2: Object-Oriented Programming (Afternoon Section B)");

        // Quiz 3 - Advanced Programming (Group 3)
        InsertIntoDatabase.insertQuiz(conn, "Data Structures", "Arrays, Lists, and Trees", 3);
        System.out.println("  ✓ Quiz 3: Data Structures (Advanced Programming)");

        // Quiz 4 - Beginners Class (Group 4)
        InsertIntoDatabase.insertQuiz(conn, "Introduction to Programming", "Basic programming concepts", 4);
        System.out.println("  ✓ Quiz 4: Introduction to Programming (Beginners Class)");

        // Quiz 5 - (Note: quiz5 was created but is missing group linkage in DB - skip)

        // Quiz 9 - GROUPB (Group 6)
        InsertIntoDatabase.insertQuiz(conn, "quiz1", "desc", 6);
        System.out.println("  ✓ Quiz 9: quiz1 (GROUPB)");

        // Quiz 10 - groupA (Group 7)
        InsertIntoDatabase.insertQuiz(conn, "quiz1", "desc1", 7);
        System.out.println("  ✓ Quiz 10: quiz1 (groupA)");

        // Quiz 11 - best group ever (Group 8)
        InsertIntoDatabase.insertQuiz(conn, "quiz1000", "description", 8);
        System.out.println("  ✓ Quiz 11: quiz1000 (best group ever)");

        // Quiz 12 - Intro to programming (Group 9)
        InsertIntoDatabase.insertQuiz(conn, "Data structures in java", "desc", 9);
        System.out.println("  ✓ Quiz 12: Data structures in java (Intro to programming)");
    }

    private static void addQuestions(Connection conn) throws SQLException {
        // Questions for Java Basics Quiz (Quiz 1) - NOW ALL 1 POINT
        int q1 = InsertIntoDatabase.insertMcq(conn,
                "What is the correct way to declare a variable in Java?",
                "int x = 5;", "variable x = 5;", "x = 5;", "declare int x = 5;",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 1, q1);

        int q2 = InsertIntoDatabase.insertMcq(conn,
                "Which keyword is used to create a class in Java?",
                "function", "class", "object", "struct",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 1, q2);

        int q3 = InsertIntoDatabase.insertMcq(conn,
                "What is the entry point of a Java application?",
                "start() method", "main() method", "run() method", "execute() method",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 1, q3);
        System.out.println("  ✓ Quiz 1: 3 questions (1 pt each)");

        // Questions for OOP Quiz (Quiz 2) - NOW ALL 1 POINT
        int q4 = InsertIntoDatabase.insertMcq(conn,
                "Which of these is a principle of OOP?",
                "Compilation", "Encapsulation", "Execution", "Declaration",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 2, q4);

        int q5 = InsertIntoDatabase.insertMcq(conn,
                "What is inheritance in OOP?",
                "Creating multiple objects", "A class acquiring properties from another class",
                "Hiding implementation details", "Overloading methods",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 2, q5);
        System.out.println("  ✓ Quiz 2: 2 questions (1 pt each)");

        // Questions for Data Structures Quiz (Quiz 3) - NOW ALL 1 POINT
        int q6 = InsertIntoDatabase.insertMcq(conn,
                "What is the time complexity of accessing an element in an array?",
                "O(n)", "O(log n)", "O(1)", "O(n^2)",
                'C', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 3, q6);

        int q7 = InsertIntoDatabase.insertMcq(conn,
                "Which data structure uses LIFO principle?",
                "Queue", "Stack", "Array", "Tree",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 3, q7);
        System.out.println("  ✓ Quiz 3: 2 questions (1 pt each)");

        // Questions for Intro to Programming Quiz (Quiz 4) - NOW ALL 1 POINT
        int q8 = InsertIntoDatabase.insertMcq(conn,
                "What does CPU stand for?",
                "Central Processing Unit", "Computer Personal Unit",
                "Central Program Utility", "Computer Processing Unit",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 4, q8);

        int q9 = InsertIntoDatabase.insertMcq(conn,
                "What is a variable?",
                "A fixed value", "A storage location with a name",
                "A type of loop", "A programming language",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 4, q9);

        int q10 = InsertIntoDatabase.insertMcq(conn,
                "Which symbol is used for comments in Java?",
                "#", "//", "/*", "Both B and C",
                'D', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 4, q10);
        System.out.println("  ✓ Quiz 4: 3 questions (1 pt each)");

        // Question for Quiz 5 (was linked to deleted quiz, now added to Quiz 9)
        int q11 = InsertIntoDatabase.insertMcq(conn,
                "how old are u",
                "10", "12", "15", "16",
                'B', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 9, q11);

        int q12 = InsertIntoDatabase.insertMcq(conn,
                "how many siblings?",
                "1", "2", "3", "4",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 9, q12);
        System.out.println("  ✓ Quiz 9: 2 questions (1 pt each)");

        // Questions for Quiz 10 (groupA)
        int q13 = InsertIntoDatabase.insertMcq(conn,
                "amazing weather",
                "yes", "no", "maybe", "idk",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 10, q13);

        int q14 = InsertIntoDatabase.insertMcq(conn,
                "how old are u",
                "10", "11", "12", "13",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 10, q14);

        int q15 = InsertIntoDatabase.insertMcq(conn,
                "1",
                "1", "2", "3", "4",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 10, q15);
        System.out.println("  ✓ Quiz 10: 3 questions (1 pt each)");

        // Questions for Quiz 11 (best group ever)
        int q16 = InsertIntoDatabase.insertMcq(conn,
                "favourite food",
                "pizza", "burger", "pasta", "sushi",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 11, q16);

        int q17 = InsertIntoDatabase.insertMcq(conn,
                "how many fingers do we have",
                "8", "9", "10", "20",
                'D', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 11, q17);
        System.out.println("  ✓ Quiz 11: 2 questions (1 pt each)");

        // Question for Quiz 12 (Intro to programming)
        int q18 = InsertIntoDatabase.insertMcq(conn,
                "question1",
                "answer1", "answer2", "answer3", "answer4",
                'A', 1);
        InsertIntoDatabase.insertQuizQuestion(conn, 12, q18);
        System.out.println("  ✓ Quiz 12: 1 question (1 pt)");
    }
}
