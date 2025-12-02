package com.example.service;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DatabaseQuizService - verifies quiz and question persistence.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseQuizServiceTest {

    private static DatabaseQuizService quizService;
    private static DatabaseUserService userService;
    private static int teacherId;
    private static int studentId;
    private static int mathQuizId;
    private static int scienceQuizId;
    private static int question1Id;
    private static int question2Id;

    @BeforeAll
    static void setupService() {
        userService = new DatabaseUserService();
        quizService = new DatabaseQuizService(userService);

        // Get teacher and student IDs
        Optional<User> teacher = userService.getUserByEmail("teacher@test.com");
        Optional<User> student = userService.getUserByEmail("student@test.com");

        assertTrue(teacher.isPresent(), "Teacher must exist for quiz tests");
        assertTrue(student.isPresent(), "Student must exist for quiz tests");

        teacherId = teacher.get().getId();
        studentId = student.get().getId();

        System.out.println("✓ DatabaseQuizService test initialized (Teacher ID: " + teacherId + ", Student ID: " + studentId + ")");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Create Math quiz")
    void testCreateMathQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Math Quiz 1");
        quiz.setDescription("Basic arithmetic");
        quiz.setTeacherId(teacherId);

        Quiz created = quizService.createQuiz(quiz);

        assertNotNull(created, "Created quiz should not be null");
        assertTrue(created.getId() > 0, "Quiz should have a valid ID");
        assertEquals("Math Quiz 1", created.getTitle());
        assertEquals("Basic arithmetic", created.getDescription());
        assertEquals(teacherId, created.getTeacherId());

        mathQuizId = created.getId();
        System.out.println("✓ Created Math Quiz 1 (ID: " + mathQuizId + ")");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Create Science quiz")
    void testCreateScienceQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Science Quiz 1");
        quiz.setDescription("Basic chemistry");
        quiz.setTeacherId(teacherId);

        Quiz created = quizService.createQuiz(quiz);

        assertNotNull(created, "Created quiz should not be null");
        scienceQuizId = created.getId();
        System.out.println("✓ Created Science Quiz 1 (ID: " + scienceQuizId + ")");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Get quiz by ID")
    void testGetQuizById() {
        Optional<Quiz> quiz = quizService.getQuizById(mathQuizId);

        assertTrue(quiz.isPresent(), "Math quiz should be found");
        assertEquals("Math Quiz 1", quiz.get().getTitle());
        assertEquals("Basic arithmetic", quiz.get().getDescription());
        assertEquals(teacherId, quiz.get().getTeacherId());
        System.out.println("✓ Retrieved Math Quiz 1 by ID");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Get all quizzes")
    void testGetAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();

        assertNotNull(quizzes, "Quiz list should not be null");
        assertTrue(quizzes.size() >= 2, "Should have at least 2 quizzes");
        System.out.println("✓ Retrieved " + quizzes.size() + " quizzes");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Get quizzes by teacher")
    void testGetQuizzesByTeacher() {
        List<Quiz> teacherQuizzes = quizService.getQuizzesByTeacher(teacherId);

        assertNotNull(teacherQuizzes, "Teacher quiz list should not be null");
        assertTrue(teacherQuizzes.size() >= 2, "Teacher should have at least 2 quizzes");
        assertTrue(teacherQuizzes.stream().allMatch(q -> q.getTeacherId() == teacherId),
                "All quizzes should belong to this teacher");
        System.out.println("✓ Teacher has " + teacherQuizzes.size() + " quizzes");
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Add question to Math quiz")
    void testAddQuestionToMathQuiz() {
        Question question = new Question();
        question.setText("What is 2 + 2?");
        question.setOptionA("3");
        question.setOptionB("4");
        question.setOptionC("5");
        question.setOptionD("6");
        question.setCorrectAnswer('B');
        question.setAssignedScore(10);

        Question added = quizService.addQuestion(mathQuizId, question);

        assertNotNull(added, "Added question should not be null");
        assertTrue(added.getId() > 0, "Question should have a valid ID");
        assertEquals("What is 2 + 2?", added.getText());
        assertEquals('B', added.getCorrectAnswer());
        assertEquals(10, added.getAssignedScore());

        question1Id = added.getId();
        System.out.println("✓ Added question 1 to Math Quiz (Question ID: " + question1Id + ")");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Add second question to Math quiz")
    void testAddSecondQuestionToMathQuiz() {
        Question question = new Question();
        question.setText("What is 5 * 3?");
        question.setOptionA("8");
        question.setOptionB("12");
        question.setOptionC("15");
        question.setOptionD("20");
        question.setCorrectAnswer('C');
        question.setAssignedScore(10);

        Question added = quizService.addQuestion(mathQuizId, question);

        assertNotNull(added, "Added question should not be null");
        question2Id = added.getId();
        System.out.println("✓ Added question 2 to Math Quiz (Question ID: " + question2Id + ")");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Get questions by quiz")
    void testGetQuestionsByQuiz() {
        List<Question> questions = quizService.getQuestionsByQuiz(mathQuizId);

        assertNotNull(questions, "Question list should not be null");
        assertEquals(2, questions.size(), "Math quiz should have 2 questions");

        Question q1 = questions.stream()
                .filter(q -> q.getText().equals("What is 2 + 2?"))
                .findFirst()
                .orElse(null);
        assertNotNull(q1, "Should find first question");
        assertEquals('B', q1.getCorrectAnswer());

        System.out.println("✓ Retrieved 2 questions for Math Quiz");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Update quiz")
    void testUpdateQuiz() {
        Optional<Quiz> quiz = quizService.getQuizById(mathQuizId);
        assertTrue(quiz.isPresent(), "Math quiz should exist");

        Quiz updated = quiz.get();
        updated.setTitle("Math Quiz 1 - Updated");
        updated.setDescription("Basic arithmetic with updates");

        boolean result = quizService.updateQuiz(updated);
        assertTrue(result, "Quiz update should succeed");

        // Verify changes persisted
        Optional<Quiz> reloaded = quizService.getQuizById(mathQuizId);
        assertTrue(reloaded.isPresent(), "Updated quiz should be found");
        assertEquals("Math Quiz 1 - Updated", reloaded.get().getTitle());
        assertEquals("Basic arithmetic with updates", reloaded.get().getDescription());
        System.out.println("✓ Updated Math Quiz title and description");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Update question")
    void testUpdateQuestion() {
        List<Question> questions = quizService.getQuestionsByQuiz(mathQuizId);
        Question question = questions.get(0);

        question.setAssignedScore(15); // Change score from 10 to 15

        boolean result = quizService.updateQuestion(question);
        assertTrue(result, "Question update should succeed");

        // Verify changes persisted
        List<Question> reloaded = quizService.getQuestionsByQuiz(mathQuizId);
        Question updated = reloaded.stream()
                .filter(q -> q.getId() == question.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(updated, "Updated question should be found");
        assertEquals(15, updated.getAssignedScore());
        System.out.println("✓ Updated question score from 10 to 15");
    }

    @Test
    @Order(11)
    @DisplayName("Test 11: Assign quiz to student")
    void testAssignQuizToStudent() {
        boolean result = quizService.assignQuizToStudent(mathQuizId, studentId);
        assertTrue(result, "Quiz assignment should succeed");
        System.out.println("✓ Assigned Math Quiz to student");
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Get assigned quizzes for student")
    void testGetAssignedQuizzes() {
        List<Quiz> assigned = quizService.getAssignedQuizzes(studentId);

        assertNotNull(assigned, "Assigned quiz list should not be null");
        assertTrue(assigned.size() >= 1, "Student should have at least 1 assigned quiz");
        assertTrue(assigned.stream().anyMatch(q -> q.getId() == mathQuizId),
                "Math quiz should be in assigned list");
        System.out.println("✓ Student has " + assigned.size() + " assigned quiz(es)");
    }

    @Test
    @Order(13)
    @DisplayName("Test 13: Check if quiz is assigned")
    void testIsQuizAssigned() {
        boolean assigned = quizService.isQuizAssigned(mathQuizId, studentId);
        assertTrue(assigned, "Math quiz should be assigned to student");

        boolean notAssigned = quizService.isQuizAssigned(scienceQuizId, studentId);
        assertFalse(notAssigned, "Science quiz should not be assigned to student");
        System.out.println("✓ Quiz assignment status checked correctly");
    }

    @Test
    @Order(14)
    @DisplayName("Test 14: Get students assigned to quiz")
    void testGetStudentsAssignedToQuiz() {
        List<User> students = quizService.getStudentsAssignedToQuiz(mathQuizId);

        assertNotNull(students, "Student list should not be null");
        assertTrue(students.size() >= 1, "At least 1 student should be assigned");
        assertTrue(students.stream().anyMatch(s -> s.getId() == studentId),
                "Our test student should be in the list");
        System.out.println("✓ Math Quiz has " + students.size() + " student(s) assigned");
    }

    @Test
    @Order(15)
    @DisplayName("Test 15: Search quizzes by title")
    void testSearchQuizzes() {
        List<Quiz> results = quizService.searchQuizzes("Math");

        assertNotNull(results, "Search results should not be null");
        assertTrue(results.size() >= 1, "Should find at least Math quiz");
        assertTrue(results.stream().anyMatch(q -> q.getTitle().contains("Math")),
                "Should find Math quiz in results");
        System.out.println("✓ Search for 'Math' found " + results.size() + " quiz(zes)");
    }

    @Test
    @Order(16)
    @DisplayName("Test 16: Delete question")
    void testDeleteQuestion() {
        boolean result = quizService.deleteQuestion(question2Id);
        assertTrue(result, "Question deletion should succeed");

        // Verify question is gone
        List<Question> questions = quizService.getQuestionsByQuiz(mathQuizId);
        assertEquals(1, questions.size(), "Math quiz should now have 1 question");
        assertFalse(questions.stream().anyMatch(q -> q.getId() == question2Id),
                "Deleted question should not be in list");
        System.out.println("✓ Deleted question 2 from Math Quiz");
    }

    @Test
    @Order(17)
    @DisplayName("Test 17: Get total quiz count")
    void testGetTotalQuizCount() {
        int count = quizService.getTotalQuizCount();
        assertTrue(count >= 2, "Should have at least 2 quizzes");
        System.out.println("✓ Total quiz count: " + count);
    }

    @Test
    @Order(18)
    @DisplayName("Test 18: Delete quiz")
    void testDeleteQuiz() {
        boolean result = quizService.deleteQuiz(scienceQuizId);
        assertTrue(result, "Quiz deletion should succeed");

        // Verify quiz is gone
        Optional<Quiz> deleted = quizService.getQuizById(scienceQuizId);
        assertFalse(deleted.isPresent(), "Deleted quiz should not be found");
        System.out.println("✓ Deleted Science Quiz");
    }
}
