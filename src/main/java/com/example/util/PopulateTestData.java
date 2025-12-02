package com.example.util;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class to populate the database with test data.
 * Run this once to create sample users, groups, and quizzes for testing.
 */
public class PopulateTestData {

    public static void main(String[] args) {
        System.out.println("Starting to populate test data...");

        try {
            Connection conn = DatabaseManager.connectWithDatabase();
            if (conn == null) {
                System.err.println("Failed to connect to database!");
                return;
            }

            // Create all tables first
            DatabaseManager.createAllTables(conn);

            System.out.println("\n=== Creating Users ===");
            createUsers(conn);

            System.out.println("\n=== Creating Groups ===");
            createGroups(conn);

            System.out.println("\n=== Enrolling Students ===");
            enrollStudents(conn);

            System.out.println("\n=== Creating Quizzes ===");
            createQuizzes(conn);

            System.out.println("\n=== Adding Questions to Quizzes ===");
            addQuestions(conn);

            System.out.println("\n✅ Test data population complete!");
            System.out.println("\n📝 Login Credentials:");
            System.out.println("Admin: admin@quiz.com / admin123");
            System.out.println("Teacher 1: alice.teacher@quiz.com / teacher123");
            System.out.println("Teacher 2: bob.teacher@quiz.com / teacher123");
            System.out.println("Student 1: charlie.student@quiz.com / student123");
            System.out.println("Student 2: diana.student@quiz.com / student123");
            System.out.println("Student 3: eve.student@quiz.com / student123");
            System.out.println("Student 4: frank.student@quiz.com / student123");
            System.out.println("Student 5: grace.student@quiz.com / student123");

        } catch (SQLException e) {
            System.err.println("Error populating test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createUsers(Connection conn) throws SQLException {
        // Admin
        InsertIntoDatabase.insertPeople(conn, "Admin", "User", "admin@quiz.com", "admin123", "admin");
        System.out.println("✓ Created Admin user");

        // Teachers
        InsertIntoDatabase.insertPeople(conn, "Alice", "Teacher", "alice.teacher@quiz.com", "teacher123", "teacher");
        InsertIntoDatabase.insertPeople(conn, "Bob", "Teacher", "bob.teacher@quiz.com", "teacher123", "teacher");
        System.out.println("✓ Created 2 teachers");

        // Students
        InsertIntoDatabase.insertPeople(conn, "Charlie", "Student", "charlie.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Diana", "Student", "diana.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Eve", "Student", "eve.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Frank", "Student", "frank.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Grace", "Student", "grace.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Henry", "Student", "henry.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Ivy", "Student", "ivy.student@quiz.com", "student123", "student");
        InsertIntoDatabase.insertPeople(conn, "Jack", "Student", "jack.student@quiz.com", "student123", "student");
        System.out.println("✓ Created 8 students");
    }

    private static void createGroups(Connection conn) throws SQLException {
        // Group 1 - Alice's class (Morning Section)
        InsertIntoDatabase.insertGroup(conn, "Morning Section A", "alice.teacher@quiz.com");
        System.out.println("✓ Created group: Morning Section A (Teacher: Alice)");

        // Group 2 - Alice's class (Afternoon Section)
        InsertIntoDatabase.insertGroup(conn, "Afternoon Section B", "alice.teacher@quiz.com");
        System.out.println("✓ Created group: Afternoon Section B (Teacher: Alice)");

        // Group 3 - Bob's class (Advanced Programming)
        InsertIntoDatabase.insertGroup(conn, "Advanced Programming", "bob.teacher@quiz.com");
        System.out.println("✓ Created group: Advanced Programming (Teacher: Bob)");

        // Group 4 - Bob's class (Beginners Class)
        InsertIntoDatabase.insertGroup(conn, "Beginners Class", "bob.teacher@quiz.com");
        System.out.println("✓ Created group: Beginners Class (Teacher: Bob)");
    }

    private static void enrollStudents(Connection conn) throws SQLException {
        // Morning Section A (Alice) - Charlie, Diana, Eve
        InsertIntoDatabase.insertEnrollment(conn, "Morning Section A", "charlie.student@quiz.com");
        InsertIntoDatabase.insertEnrollment(conn, "Morning Section A", "diana.student@quiz.com");
        InsertIntoDatabase.insertEnrollment(conn, "Morning Section A", "eve.student@quiz.com");
        System.out.println("✓ Enrolled 3 students in Morning Section A");

        // Afternoon Section B (Alice) - Frank, Grace
        InsertIntoDatabase.insertEnrollment(conn, "Afternoon Section B", "frank.student@quiz.com");
        InsertIntoDatabase.insertEnrollment(conn, "Afternoon Section B", "grace.student@quiz.com");
        System.out.println("✓ Enrolled 2 students in Afternoon Section B");

        // Advanced Programming (Bob) - Diana, Eve, Henry
        InsertIntoDatabase.insertEnrollment(conn, "Advanced Programming", "diana.student@quiz.com");
        InsertIntoDatabase.insertEnrollment(conn, "Advanced Programming", "eve.student@quiz.com");
        InsertIntoDatabase.insertEnrollment(conn, "Advanced Programming", "henry.student@quiz.com");
        System.out.println("✓ Enrolled 3 students in Advanced Programming");

        // Beginners Class (Bob) - Ivy, Jack
        InsertIntoDatabase.insertEnrollment(conn, "Beginners Class", "ivy.student@quiz.com");
        InsertIntoDatabase.insertEnrollment(conn, "Beginners Class", "jack.student@quiz.com");
        System.out.println("✓ Enrolled 2 students in Beginners Class");
    }

    private static void createQuizzes(Connection conn) throws SQLException {
        // Quiz 1 - Morning Section A (Alice)
        InsertIntoDatabase.insertQuiz(conn, "Java Basics Quiz", "Test your knowledge of Java fundamentals", "Morning Section A");
        System.out.println("✓ Created quiz: Java Basics Quiz (Morning Section A)");

        // Quiz 2 - Afternoon Section B (Alice)
        InsertIntoDatabase.insertQuiz(conn, "Object-Oriented Programming", "Understanding OOP concepts in Java", "Afternoon Section B");
        System.out.println("✓ Created quiz: Object-Oriented Programming (Afternoon Section B)");

        // Quiz 3 - Advanced Programming (Bob)
        InsertIntoDatabase.insertQuiz(conn, "Data Structures", "Arrays, Lists, and Trees", "Advanced Programming");
        System.out.println("✓ Created quiz: Data Structures (Advanced Programming)");

        // Quiz 4 - Beginners Class (Bob)
        InsertIntoDatabase.insertQuiz(conn, "Introduction to Programming", "Basic programming concepts", "Beginners Class");
        System.out.println("✓ Created quiz: Introduction to Programming (Beginners Class)");
    }

    private static void addQuestions(Connection conn) throws SQLException {
        // Get quiz IDs
        int javaBasicsQuizId = com.example.database.RetrieveFromDatabase.getQuizId(conn, "Java Basics Quiz");
        int oopQuizId = com.example.database.RetrieveFromDatabase.getQuizId(conn, "Object-Oriented Programming");
        int dataStructuresQuizId = com.example.database.RetrieveFromDatabase.getQuizId(conn, "Data Structures");
        int introQuizId = com.example.database.RetrieveFromDatabase.getQuizId(conn, "Introduction to Programming");

        // Add questions to Java Basics Quiz
        int q1 = InsertIntoDatabase.insertMcq(conn,
            "What is the correct way to declare a variable in Java?",
            "int x = 5;", "variable x = 5;", "x = 5;", "declare int x = 5;",
            'A', 10);
        InsertIntoDatabase.insertQuizQuestion(conn, javaBasicsQuizId, q1);

        int q2 = InsertIntoDatabase.insertMcq(conn,
            "Which keyword is used to create a class in Java?",
            "function", "class", "object", "struct",
            'B', 10);
        InsertIntoDatabase.insertQuizQuestion(conn, javaBasicsQuizId, q2);

        int q3 = InsertIntoDatabase.insertMcq(conn,
            "What is the entry point of a Java application?",
            "start() method", "main() method", "run() method", "execute() method",
            'B', 10);
        InsertIntoDatabase.insertQuizQuestion(conn, javaBasicsQuizId, q3);
        System.out.println("✓ Added 3 questions to Java Basics Quiz");

        // Add questions to OOP Quiz
        int q4 = InsertIntoDatabase.insertMcq(conn,
            "Which of these is a principle of OOP?",
            "Compilation", "Encapsulation", "Execution", "Declaration",
            'B', 15);
        InsertIntoDatabase.insertQuizQuestion(conn, oopQuizId, q4);

        int q5 = InsertIntoDatabase.insertMcq(conn,
            "What is inheritance in OOP?",
            "Creating multiple objects", "A class acquiring properties from another class",
            "Hiding implementation details", "Overloading methods",
            'B', 15);
        InsertIntoDatabase.insertQuizQuestion(conn, oopQuizId, q5);
        System.out.println("✓ Added 2 questions to Object-Oriented Programming");

        // Add questions to Data Structures Quiz
        int q6 = InsertIntoDatabase.insertMcq(conn,
            "What is the time complexity of accessing an element in an array?",
            "O(n)", "O(log n)", "O(1)", "O(n^2)",
            'C', 20);
        InsertIntoDatabase.insertQuizQuestion(conn, dataStructuresQuizId, q6);

        int q7 = InsertIntoDatabase.insertMcq(conn,
            "Which data structure uses LIFO principle?",
            "Queue", "Stack", "Array", "Tree",
            'B', 20);
        InsertIntoDatabase.insertQuizQuestion(conn, dataStructuresQuizId, q7);
        System.out.println("✓ Added 2 questions to Data Structures");

        // Add questions to Intro to Programming Quiz
        int q8 = InsertIntoDatabase.insertMcq(conn,
            "What does CPU stand for?",
            "Central Processing Unit", "Computer Personal Unit",
            "Central Program Utility", "Computer Processing Unit",
            'A', 5);
        InsertIntoDatabase.insertQuizQuestion(conn, introQuizId, q8);

        int q9 = InsertIntoDatabase.insertMcq(conn,
            "What is a variable?",
            "A fixed value", "A storage location with a name",
            "A type of loop", "A programming language",
            'B', 5);
        InsertIntoDatabase.insertQuizQuestion(conn, introQuizId, q9);

        int q10 = InsertIntoDatabase.insertMcq(conn,
            "Which symbol is used for comments in Java?",
            "#", "//", "/*", "Both B and C",
            'D', 5);
        InsertIntoDatabase.insertQuizQuestion(conn, introQuizId, q10);
        System.out.println("✓ Added 3 questions to Introduction to Programming");
    }
}
