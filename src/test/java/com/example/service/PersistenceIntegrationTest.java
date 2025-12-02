package com.example.service;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;
import com.example.quizlogic.QuizAttempt;
import com.example.quizlogic.StudentAnswer;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test simulating the complete quiz platform workflow.
 * This test verifies that all data persists correctly through the entire lifecycle.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersistenceIntegrationTest {

    private static DatabaseAuthService authService;
    private static DatabaseUserService userService;
    private static DatabaseQuizService quizService;
    private static DatabaseAttemptService attemptService;

    private static User admin;
    private static User teacher;
    private static User student1;
    private static User student2;
    private static Quiz mathQuiz;
    private static Quiz scienceQuiz;
    private static Question mathQ1;
    private static Question mathQ2;
    private static Question scienceQ1;

    @BeforeAll
    static void setupCompleteSystem() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("INTEGRATION TEST: Complete Quiz Platform Persistence Flow");
        System.out.println("=".repeat(80) + "\n");

        // Delete test database to start fresh
        File testDb = new File("test_group5Quiz.db");
        if (testDb.exists()) {
            testDb.delete();
            System.out.println("✓ Cleared previous test database\n");
        }

        // Initialize all services
        authService = new DatabaseAuthService();
        userService = new DatabaseUserService();
        quizService = new DatabaseQuizService(userService);
        attemptService = new DatabaseAttemptService();

        System.out.println("✓ All services initialized\n");
    }

    @Test
    @Order(1)
    @DisplayName("Phase 1: User Registration & Authentication")
    void phase1_UserRegistrationAndAuth() {
        System.out.println("--- PHASE 1: USER REGISTRATION & AUTHENTICATION ---\n");

        // Admin registers
        admin = new User("admin@school.com", "admin123", "Alice", "Admin", User.Role.ADMIN);
        assertTrue(authService.register(admin), "Admin registration should succeed");
        System.out.println("✓ Admin registered: Alice Admin (admin@school.com)");

        // Teacher registers
        teacher = new User("teacher@school.com", "teach123", "Bob", "Teacher", User.Role.TEACHER);
        assertTrue(authService.register(teacher), "Teacher registration should succeed");
        System.out.println("✓ Teacher registered: Bob Teacher (teacher@school.com)");

        // Two students register
        student1 = new User("jane@school.com", "jane123", "Jane", "Doe", User.Role.STUDENT);
        assertTrue(authService.register(student1), "Student 1 registration should succeed");
        System.out.println("✓ Student 1 registered: Jane Doe (jane@school.com)");

        student2 = new User("john@school.com", "john123", "John", "Smith", User.Role.STUDENT);
        assertTrue(authService.register(student2), "Student 2 registration should succeed");
        System.out.println("✓ Student 2 registered: John Smith (john@school.com)");

        // Verify all can login
        Optional<User> adminLogin = authService.login("admin@school.com", "admin123");
        assertTrue(adminLogin.isPresent(), "Admin should login successfully");
        admin = adminLogin.get(); // Update with ID from database
        System.out.println("✓ Admin login successful (ID: " + admin.getId() + ")");

        Optional<User> teacherLogin = authService.login("teacher@school.com", "teach123");
        assertTrue(teacherLogin.isPresent(), "Teacher should login successfully");
        teacher = teacherLogin.get();
        System.out.println("✓ Teacher login successful (ID: " + teacher.getId() + ")");

        Optional<User> student1Login = authService.login("jane@school.com", "jane123");
        assertTrue(student1Login.isPresent(), "Student 1 should login successfully");
        student1 = student1Login.get();
        System.out.println("✓ Student 1 login successful (ID: " + student1.getId() + ")");

        Optional<User> student2Login = authService.login("john@school.com", "john123");
        assertTrue(student2Login.isPresent(), "Student 2 should login successfully");
        student2 = student2Login.get();
        System.out.println("✓ Student 2 login successful (ID: " + student2.getId() + ")");

        System.out.println("\n✅ Phase 1 Complete: 4 users registered and authenticated\n");
    }

    @Test
    @Order(2)
    @DisplayName("Phase 2: Admin User Management")
    void phase2_AdminUserManagement() {
        System.out.println("--- PHASE 2: ADMIN USER MANAGEMENT ---\n");

        // Admin views all users
        List<User> allUsers = userService.getAllUsers();
        assertEquals(4, allUsers.size(), "Should have 4 users total");
        System.out.println("✓ Admin views all users: " + allUsers.size() + " users found");

        // Admin counts users by role
        int adminCount = userService.countUsersByRole(User.Role.ADMIN);
        int teacherCount = userService.countUsersByRole(User.Role.TEACHER);
        int studentCount = userService.countUsersByRole(User.Role.STUDENT);

        assertEquals(1, adminCount, "Should have 1 admin");
        assertEquals(1, teacherCount, "Should have 1 teacher");
        assertEquals(2, studentCount, "Should have 2 students");
        System.out.println("✓ User counts: " + adminCount + " admin, " + teacherCount + " teacher, " + studentCount + " students");

        // Admin searches for a user
        List<User> searchResults = userService.searchUsers("jane");
        assertTrue(searchResults.size() >= 1, "Should find Jane");
        System.out.println("✓ Admin search for 'jane' found " + searchResults.size() + " result(s)");

        System.out.println("\n✅ Phase 2 Complete: Admin management verified\n");
    }

    @Test
    @Order(3)
    @DisplayName("Phase 3: Teacher Creates Quizzes")
    void phase3_TeacherCreatesQuizzes() {
        System.out.println("--- PHASE 3: TEACHER CREATES QUIZZES ---\n");

        // Teacher creates Math Quiz
        mathQuiz = new Quiz();
        mathQuiz.setTitle("Mathematics Final Exam");
        mathQuiz.setDescription("Covers algebra and arithmetic");
        mathQuiz.setTeacherId(teacher.getId());
        mathQuiz = quizService.createQuiz(mathQuiz);
        assertTrue(mathQuiz.getId() > 0, "Math quiz should have valid ID");
        System.out.println("✓ Teacher created Math Quiz (ID: " + mathQuiz.getId() + ")");

        // Teacher adds questions to Math Quiz
        mathQ1 = new Question();
        mathQ1.setText("What is 15 + 27?");
        mathQ1.setOptionA("32");
        mathQ1.setOptionB("42");
        mathQ1.setOptionC("52");
        mathQ1.setOptionD("62");
        mathQ1.setCorrectAnswer('B');
        mathQ1.setAssignedScore(10);
        mathQ1 = quizService.addQuestion(mathQuiz.getId(), mathQ1);
        System.out.println("✓ Added Math Question 1 (ID: " + mathQ1.getId() + ")");

        mathQ2 = new Question();
        mathQ2.setText("What is 8 * 7?");
        mathQ2.setOptionA("54");
        mathQ2.setOptionB("56");
        mathQ2.setOptionC("58");
        mathQ2.setOptionD("60");
        mathQ2.setCorrectAnswer('B');
        mathQ2.setAssignedScore(15);
        mathQ2 = quizService.addQuestion(mathQuiz.getId(), mathQ2);
        System.out.println("✓ Added Math Question 2 (ID: " + mathQ2.getId() + ")");

        // Teacher creates Science Quiz
        scienceQuiz = new Quiz();
        scienceQuiz.setTitle("Science Quiz 1");
        scienceQuiz.setDescription("Basic chemistry");
        scienceQuiz.setTeacherId(teacher.getId());
        scienceQuiz = quizService.createQuiz(scienceQuiz);
        System.out.println("✓ Teacher created Science Quiz (ID: " + scienceQuiz.getId() + ")");

        scienceQ1 = new Question();
        scienceQ1.setText("What is the chemical symbol for water?");
        scienceQ1.setOptionA("H2O");
        scienceQ1.setOptionB("CO2");
        scienceQ1.setOptionC("O2");
        scienceQ1.setOptionD("N2");
        scienceQ1.setCorrectAnswer('A');
        scienceQ1.setAssignedScore(10);
        scienceQ1 = quizService.addQuestion(scienceQuiz.getId(), scienceQ1);
        System.out.println("✓ Added Science Question 1 (ID: " + scienceQ1.getId() + ")");

        // Verify quizzes persist
        List<Quiz> teacherQuizzes = quizService.getQuizzesByTeacher(teacher.getId());
        assertEquals(2, teacherQuizzes.size(), "Teacher should have 2 quizzes");
        System.out.println("✓ Teacher has " + teacherQuizzes.size() + " quizzes");

        System.out.println("\n✅ Phase 3 Complete: 2 quizzes with 3 questions created\n");
    }

    @Test
    @Order(4)
    @DisplayName("Phase 4: Teacher Assigns Quizzes")
    void phase4_TeacherAssignsQuizzes() {
        System.out.println("--- PHASE 4: TEACHER ASSIGNS QUIZZES ---\n");

        // Assign Math Quiz to both students
        assertTrue(quizService.assignQuizToStudent(mathQuiz.getId(), student1.getId()),
                "Math quiz assignment to Jane should succeed");
        System.out.println("✓ Assigned Math Quiz to Jane");

        assertTrue(quizService.assignQuizToStudent(mathQuiz.getId(), student2.getId()),
                "Math quiz assignment to John should succeed");
        System.out.println("✓ Assigned Math Quiz to John");

        // Assign Science Quiz to only Jane
        assertTrue(quizService.assignQuizToStudent(scienceQuiz.getId(), student1.getId()),
                "Science quiz assignment to Jane should succeed");
        System.out.println("✓ Assigned Science Quiz to Jane");

        // Verify assignments
        List<Quiz> janeQuizzes = quizService.getAssignedQuizzes(student1.getId());
        assertEquals(2, janeQuizzes.size(), "Jane should have 2 assigned quizzes");
        System.out.println("✓ Jane has " + janeQuizzes.size() + " assigned quizzes");

        List<Quiz> johnQuizzes = quizService.getAssignedQuizzes(student2.getId());
        assertEquals(1, johnQuizzes.size(), "John should have 1 assigned quiz");
        System.out.println("✓ John has " + johnQuizzes.size() + " assigned quiz");

        System.out.println("\n✅ Phase 4 Complete: Quizzes assigned to students\n");
    }

    @Test
    @Order(5)
    @DisplayName("Phase 5: Students Take Quizzes")
    void phase5_StudentsTakeQuizzes() {
        System.out.println("--- PHASE 5: STUDENTS TAKE QUIZZES ---\n");

        // Jane takes Math Quiz - Gets both correct (25 points)
        List<StudentAnswer> janeAnswers1 = new ArrayList<>();
        janeAnswers1.add(new StudentAnswer(mathQ1.getId(), 'B', true, 10, 10, 1));
        janeAnswers1.add(new StudentAnswer(mathQ2.getId(), 'B', true, 15, 15, 1));
        QuizAttempt janeAttempt1 = new QuizAttempt(mathQuiz.getId(), student1.getId(), janeAnswers1, 25, LocalDate.now());
        attemptService.saveAttempt(janeAttempt1);
        System.out.println("✓ Jane completed Math Quiz - Score: 25/25 (100%)");

        // John takes Math Quiz - Gets one correct, one wrong (10 points)
        List<StudentAnswer> johnAnswers1 = new ArrayList<>();
        johnAnswers1.add(new StudentAnswer(mathQ1.getId(), 'B', true, 10, 10, 1));
        johnAnswers1.add(new StudentAnswer(mathQ2.getId(), 'A', false, 15, 0, 1));
        QuizAttempt johnAttempt1 = new QuizAttempt(mathQuiz.getId(), student2.getId(), johnAnswers1, 10, LocalDate.now());
        attemptService.saveAttempt(johnAttempt1);
        System.out.println("✓ John completed Math Quiz - Score: 10/25 (40%)");

        // Jane takes Science Quiz - Gets it correct (10 points)
        List<StudentAnswer> janeAnswers2 = new ArrayList<>();
        janeAnswers2.add(new StudentAnswer(scienceQ1.getId(), 'A', true, 10, 10, 1));
        QuizAttempt janeAttempt2 = new QuizAttempt(scienceQuiz.getId(), student1.getId(), janeAnswers2, 10, LocalDate.now());
        attemptService.saveAttempt(janeAttempt2);
        System.out.println("✓ Jane completed Science Quiz - Score: 10/10 (100%)");

        // John retakes Math Quiz - Gets both correct this time (25 points)
        List<StudentAnswer> johnAnswers2 = new ArrayList<>();
        johnAnswers2.add(new StudentAnswer(mathQ1.getId(), 'B', true, 10, 10, 2));
        johnAnswers2.add(new StudentAnswer(mathQ2.getId(), 'B', true, 15, 15, 2));
        QuizAttempt johnAttempt2 = new QuizAttempt(mathQuiz.getId(), student2.getId(), johnAnswers2, 25, LocalDate.now());
        attemptService.saveAttempt(johnAttempt2);
        System.out.println("✓ John retook Math Quiz - Score: 25/25 (100%)");

        System.out.println("\n✅ Phase 5 Complete: 4 quiz attempts recorded\n");
    }

    @Test
    @Order(6)
    @DisplayName("Phase 6: Verify Student Results & History")
    void phase6_VerifyStudentResults() {
        System.out.println("--- PHASE 6: VERIFY STUDENT RESULTS & HISTORY ---\n");

        // Check Jane's history
        List<QuizAttempt> janeAttempts = attemptService.getAttemptsByStudent(student1.getId());
        assertEquals(2, janeAttempts.size(), "Jane should have 2 attempts");
        System.out.println("✓ Jane has " + janeAttempts.size() + " quiz attempts in history");

        // Check John's history
        List<QuizAttempt> johnAttempts = attemptService.getAttemptsByStudent(student2.getId());
        assertEquals(2, johnAttempts.size(), "John should have 2 attempts");
        System.out.println("✓ John has " + johnAttempts.size() + " quiz attempts in history");

        // Check John's best score on Math Quiz
        Optional<Integer> johnBestScore = attemptService.getBestScore(student2.getId(), mathQuiz.getId());
        assertTrue(johnBestScore.isPresent(), "John's best score should exist");
        assertEquals(25, johnBestScore.get(), "John's best score should be 25");
        System.out.println("✓ John's best score on Math Quiz: " + johnBestScore.get() + "/25");

        // Verify both students attempted Math Quiz
        assertTrue(attemptService.hasStudentAttemptedQuiz(student1.getId(), mathQuiz.getId()),
                "Jane should have attempted Math Quiz");
        assertTrue(attemptService.hasStudentAttemptedQuiz(student2.getId(), mathQuiz.getId()),
                "John should have attempted Math Quiz");
        System.out.println("✓ Both students have attempted Math Quiz");

        System.out.println("\n✅ Phase 6 Complete: Student results verified\n");
    }

    @Test
    @Order(7)
    @DisplayName("Phase 7: Teacher Views Results & Analytics")
    void phase7_TeacherViewsResults() {
        System.out.println("--- PHASE 7: TEACHER VIEWS RESULTS & ANALYTICS ---\n");

        // Teacher views all attempts on Math Quiz
        List<QuizAttempt> mathAttempts = attemptService.getAttemptsByQuiz(mathQuiz.getId());
        assertEquals(3, mathAttempts.size(), "Math Quiz should have 3 attempts total");
        System.out.println("✓ Math Quiz has " + mathAttempts.size() + " total attempts");

        // Calculate average score for Math Quiz
        double avgScore = attemptService.getAverageScoreForQuiz(mathQuiz.getId());
        // Jane: 25/25 = 100%, John attempt 1: 10/25 = 40%, John attempt 2: 25/25 = 100%
        // Average = (100 + 40 + 100) / 3 = 80%
        assertEquals(80.0, avgScore, 0.1, "Average score should be 80%");
        System.out.println("✓ Math Quiz average score: " + String.format("%.1f%%", avgScore));

        // Get students assigned to Math Quiz
        List<User> mathStudents = quizService.getStudentsAssignedToQuiz(mathQuiz.getId());
        assertEquals(2, mathStudents.size(), "Math Quiz should have 2 students assigned");
        System.out.println("✓ Math Quiz has " + mathStudents.size() + " students assigned");

        System.out.println("\n✅ Phase 7 Complete: Teacher analytics verified\n");
    }

    @Test
    @Order(8)
    @DisplayName("Phase 8: Admin Views Platform Statistics")
    void phase8_AdminViewsStatistics() {
        System.out.println("--- PHASE 8: ADMIN VIEWS PLATFORM STATISTICS ---\n");

        // Total user count
        int totalUsers = userService.getTotalUserCount();
        assertEquals(4, totalUsers, "Should have 4 users total");
        System.out.println("✓ Total users: " + totalUsers);

        // Total quiz count
        int totalQuizzes = quizService.getTotalQuizCount();
        assertEquals(2, totalQuizzes, "Should have 2 quizzes total");
        System.out.println("✓ Total quizzes: " + totalQuizzes);

        // Total attempt count
        int totalAttempts = attemptService.getTotalAttemptCount();
        assertEquals(4, totalAttempts, "Should have 4 attempts total");
        System.out.println("✓ Total attempts: " + totalAttempts);

        // Attempts today
        int attemptsToday = attemptService.getAttemptsToday();
        assertEquals(4, attemptsToday, "Should have 4 attempts today");
        System.out.println("✓ Attempts today: " + attemptsToday);

        System.out.println("\n✅ Phase 8 Complete: Admin statistics verified\n");
    }

    @AfterAll
    static void summaryReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERSISTENCE INTEGRATION TEST SUMMARY");
        System.out.println("=".repeat(80));
        System.out.println("\n✅ ALL PHASES COMPLETED SUCCESSFULLY!\n");
        System.out.println("Data persisted:");
        System.out.println("  • 4 users (1 admin, 1 teacher, 2 students)");
        System.out.println("  • 2 quizzes with 3 questions total");
        System.out.println("  • 4 quiz attempts with full answer history");
        System.out.println("\nTest database: test_group5Quiz.db (preserved for inspection)");
        System.out.println("=".repeat(80) + "\n");
    }
}
