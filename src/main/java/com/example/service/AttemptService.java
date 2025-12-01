package com.example.service;

import com.example.quizlogic.QuizAttempt;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for quiz attempt operations.
 * Person 4 will implement this with real database logic.
 */
public interface AttemptService {
    
    /**
     * Save a completed quiz attempt.
     * @return the saved attempt with assigned ID
     */
    QuizAttempt saveAttempt(QuizAttempt attempt);
    
    /**
     * Get all attempts by a student.
     */
    List<QuizAttempt> getAttemptsByStudent(int studentId);
    
    /**
     * Get all attempts for a quiz.
     */
    List<QuizAttempt> getAttemptsByQuiz(int quizId);
    
    /**
     * Get a specific attempt.
     */
    Optional<QuizAttempt> getAttemptById(int attemptId);
    
    /**
     * Get attempts by student for a specific quiz.
     */
    List<QuizAttempt> getStudentAttemptsForQuiz(int studentId, int quizId);
    
    /**
     * Check if student has attempted a quiz.
     */
    boolean hasStudentAttemptedQuiz(int studentId, int quizId);
    
    /**
     * Get the best score for a student on a quiz.
     */
    Optional<Double> getBestScore(int studentId, int quizId);
    
    // ========== Analytics ==========
    
    /**
     * Get average score for a quiz.
     */
    double getAverageScoreForQuiz(int quizId);
    
    /**
     * Get average score for a student across all quizzes.
     */
    double getAverageScoreForStudent(int studentId);
    
    /**
     * Get total attempts count.
     */
    int getTotalAttemptCount();
    
    /**
     * Get attempts count for today.
     */
    int getAttemptsToday();
    
    /**
     * Get question accuracy map (questionId -> accuracy percentage).
     */
    Map<Integer, Double> getQuestionAccuracyForQuiz(int quizId);
    
    /**
     * Get score distribution for a quiz.
     * @return Map of score range (e.g., "0-20", "21-40") to count
     */
    Map<String, Integer> getScoreDistribution(int quizId);
}
