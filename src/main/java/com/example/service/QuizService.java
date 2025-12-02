package com.example.service;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for quiz operations.
 * Person 3 (Silvia) will implement this with real database logic.
 */
public interface QuizService {
    
    // ========== Quiz CRUD ==========
    
    /**
     * Create a new quiz.
     * @return the created quiz with assigned ID
     */
    Quiz createQuiz(Quiz quiz);
    
    /**
     * Update an existing quiz.
     */
    boolean updateQuiz(Quiz quiz);
    
    /**
     * Delete a quiz by ID.
     */
    boolean deleteQuiz(int quizId);
    
    /**
     * Get a quiz by ID.
     */
    Optional<Quiz> getQuizById(int quizId);
    
    /**
     * Get all quizzes.
     */
    List<Quiz> getAllQuizzes();
    
    /**
     * Get quizzes created by a specific teacher.
     */
    List<Quiz> getQuizzesByTeacher(int teacherId);
    
    /**
     * Search quizzes by title.
     */
    List<Quiz> searchQuizzes(String query);
    
    // ========== Questions ==========
    
    /**
     * Add a question to a quiz.
     */
    Question addQuestion(int quizId, Question question);
    
    /**
     * Update a question.
     */
    boolean updateQuestion(Question question);
    
    /**
     * Delete a question.
     */
    boolean deleteQuestion(int questionId);
    
    /**
     * Get all questions for a quiz.
     */
    List<Question> getQuestionsByQuiz(int quizId);
    
    // ========== Assignments ==========
    
    /**
     * Assign a quiz to a student.
     */
    boolean assignQuizToStudent(int quizId, int studentId);
    
    /**
     * Assign a quiz to multiple students.
     */
    boolean assignQuizToStudents(int quizId, List<Integer> studentIds);
    
    /**
     * Get quizzes assigned to a student.
     */
    List<Quiz> getAssignedQuizzes(int studentId);
    
    /**
     * Get students assigned to a quiz.
     */
    List<User> getStudentsAssignedToQuiz(int quizId);
    
    /**
     * Check if a quiz is assigned to a student.
     */
    boolean isQuizAssigned(int quizId, int studentId);
    
    // ========== Statistics ==========
    
    /**
     * Get total quiz count.
     */
    int getTotalQuizCount();
    
    /**
     * Get active quiz count.
     */
    int getActiveQuizCount();
}
