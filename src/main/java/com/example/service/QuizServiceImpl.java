package com.example.service.impl;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;
import com.example.service.QuizService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Real implementation of QuizService.
 *
 * This class handles all quiz-related business logic including:
 * - Creating, updating, and deleting quizzes
 * - Managing questions within quizzes
 * - Quiz assignments to students
 * - Validation rules for quizzes and questions
 *
 * TODO: Replace placeholder implementations with actual DAO calls
 * once Person 1 completes the database layer.
 */
public class QuizServiceImpl implements QuizService {

    // TODO: Inject these DAOs when Person 1 completes them
    // private final QuizDao quizDao;
    // private final QuestionDao questionDao;
    // private final AssignmentDao assignmentDao;
    // private final UserDao userDao;

    public QuizServiceImpl() {
        // TODO: Initialize DAOs here
        // this.quizDao = new QuizDao();
        // this.questionDao = new QuestionDao();
        // this.assignmentDao = new AssignmentDao();
        // this.userDao = new UserDao();
    }

    // ==================== QUIZ CRUD ====================

    @Override
    public Quiz createQuiz(Quiz quiz) {
        // Validation
        validateQuiz(quiz);

        // Set creation timestamp
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setActive(true);

        // TODO: Replace with actual DAO call
        // return quizDao.insert(quiz);

        // Placeholder - remove when DAO is ready
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean updateQuiz(Quiz quiz) {
        // Validation
        if (quiz.getId() <= 0) {
            throw new IllegalArgumentException("Quiz ID is required for update");
        }
        validateQuiz(quiz);

        // TODO: Replace with actual DAO call
        // return quizDao.update(quiz);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean deleteQuiz(int quizId) {
        if (quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        // TODO: Also delete related questions and assignments
        // questionDao.deleteByQuizId(quizId);
        // assignmentDao.deleteByQuizId(quizId);
        // return quizDao.delete(quizId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public Optional<Quiz> getQuizById(int quizId) {
        if (quizId <= 0) {
            return Optional.empty();
        }

        // TODO: Replace with actual DAO call
        // Quiz quiz = quizDao.findById(quizId);
        // if (quiz != null) {
        //     quiz.setQuestions(questionDao.findByQuizId(quizId));
        // }
        // return Optional.ofNullable(quiz);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        // TODO: Replace with actual DAO call
        // List<Quiz> quizzes = quizDao.findAll();
        // for (Quiz quiz : quizzes) {
        //     quiz.setQuestions(questionDao.findByQuizId(quiz.getId()));
        // }
        // return quizzes;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        if (teacherId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // List<Quiz> quizzes = quizDao.findByTeacherId(teacherId);
        // for (Quiz quiz : quizzes) {
        //     quiz.setQuestions(questionDao.findByQuizId(quiz.getId()));
        // }
        // return quizzes;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<Quiz> searchQuizzes(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllQuizzes();
        }

        String searchTerm = query.toLowerCase().trim();

        // TODO: Replace with actual DAO call
        // return quizDao.searchByTitleOrDescription(searchTerm);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    // ==================== QUESTIONS ====================

    @Override
    public Question addQuestion(int quizId, Question question) {
        // Validate quiz exists
        Optional<Quiz> quizOpt = getQuizById(quizId);
        if (quizOpt.isEmpty()) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        // Validate question
        validateQuestion(question);

        // TODO: Replace with actual DAO call
        // Question savedQuestion = questionDao.insert(quizId, question);
        // return savedQuestion;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean updateQuestion(Question question) {
        if (question.getId() <= 0) {
            throw new IllegalArgumentException("Question ID is required for update");
        }
        validateQuestion(question);

        // TODO: Replace with actual DAO call
        // return questionDao.update(question);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean deleteQuestion(int questionId) {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Invalid question ID");
        }

        // TODO: Replace with actual DAO call
        // return questionDao.delete(questionId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<Question> getQuestionsByQuiz(int quizId) {
        if (quizId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // return questionDao.findByQuizId(quizId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    // ==================== ASSIGNMENTS ====================

    @Override
    public boolean assignQuizToStudent(int quizId, int studentId) {
        // Validate quiz exists
        if (getQuizById(quizId).isEmpty()) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        // Validate student exists
        // TODO: Add validation when UserDao is available
        // if (userDao.findById(studentId) == null) {
        //     throw new IllegalArgumentException("Student not found with ID: " + studentId);
        // }

        // Check if already assigned
        if (isQuizAssigned(quizId, studentId)) {
            return true; // Already assigned, consider it success
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.insert(quizId, studentId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean assignQuizToStudents(int quizId, List<Integer> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return false;
        }

        // Validate quiz exists
        if (getQuizById(quizId).isEmpty()) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        boolean allSuccess = true;
        for (Integer studentId : studentIds) {
            try {
                boolean result = assignQuizToStudent(quizId, studentId);
                allSuccess = allSuccess && result;
            } catch (Exception e) {
                // Log error but continue with other students
                System.err.println("Failed to assign quiz " + quizId + " to student " + studentId + ": " + e.getMessage());
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    @Override
    public List<Quiz> getAssignedQuizzes(int studentId) {
        if (studentId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // List<Integer> quizIds = assignmentDao.findQuizIdsByStudentId(studentId);
        // List<Quiz> quizzes = new ArrayList<>();
        // for (Integer quizId : quizIds) {
        //     getQuizById(quizId).ifPresent(quizzes::add);
        // }
        // return quizzes;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<User> getStudentsAssignedToQuiz(int quizId) {
        if (quizId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // List<Integer> studentIds = assignmentDao.findStudentIdsByQuizId(quizId);
        // List<User> students = new ArrayList<>();
        // for (Integer studentId : studentIds) {
        //     User student = userDao.findById(studentId);
        //     if (student != null) {
        //         students.add(student);
        //     }
        // }
        // return students;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean isQuizAssigned(int quizId, int studentId) {
        if (quizId <= 0 || studentId <= 0) {
            return false;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.exists(quizId, studentId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    // ==================== STATISTICS ====================

    @Override
    public int getTotalQuizCount() {
        // TODO: Replace with actual DAO call
        // return quizDao.count();

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public int getActiveQuizCount() {
        // TODO: Replace with actual DAO call
        // return quizDao.countActive();

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    // ==================== VALIDATION HELPERS ====================

    /**
     * Validates quiz data before create/update operations.
     *
     * @param quiz the quiz to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }

        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title is required");
        }

        if (quiz.getTitle().length() > 200) {
            throw new IllegalArgumentException("Quiz title cannot exceed 200 characters");
        }

        if (quiz.getTeacherId() <= 0) {
            throw new IllegalArgumentException("Valid teacher ID is required");
        }

        // Description is optional, but limit length if provided
        if (quiz.getDescription() != null && quiz.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Quiz description cannot exceed 1000 characters");
        }
    }

    /**
     * Validates question data before create/update operations.
     *
     * @param question the question to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        if (question.getText() == null || question.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }

        // Validate all options are provided
        if (question.getOptionA() == null || question.getOptionA().trim().isEmpty()) {
            throw new IllegalArgumentException("Option A is required");
        }
        if (question.getOptionB() == null || question.getOptionB().trim().isEmpty()) {
            throw new IllegalArgumentException("Option B is required");
        }
        if (question.getOptionC() == null || question.getOptionC().trim().isEmpty()) {
            throw new IllegalArgumentException("Option C is required");
        }
        if (question.getOptionD() == null || question.getOptionD().trim().isEmpty()) {
            throw new IllegalArgumentException("Option D is required");
        }

        // Validate correct answer is one of A, B, C, D
        char correctAnswer = Character.toUpperCase(question.getCorrectAnswer());
        if (correctAnswer != 'A' && correctAnswer != 'B' && correctAnswer != 'C' && correctAnswer != 'D') {
            throw new IllegalArgumentException("Correct answer must be A, B, C, or D");
        }
    }

    /**
     * Validates that a quiz has at least one question before it can be assigned.
     *
     * @param quizId the quiz ID to check
     * @return true if quiz has questions, false otherwise
     */
    public boolean quizHasQuestions(int quizId) {
        List<Question> questions = getQuestionsByQuiz(quizId);
        return questions != null && !questions.isEmpty();
    }

    /**
     * Removes a student's assignment from a quiz.
     *
     * @param quizId the quiz ID
     * @param studentId the student ID
     * @return true if unassignment was successful
     */
    public boolean unassignQuizFromStudent(int quizId, int studentId) {
        if (quizId <= 0 || studentId <= 0) {
            return false;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.delete(quizId, studentId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }
}