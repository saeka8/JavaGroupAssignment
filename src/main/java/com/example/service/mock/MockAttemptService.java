package com.example.service.mock;

import com.example.quizlogic.QuizAttempt;
import com.example.quizlogic.StudentAnswer;
import com.example.service.AttemptService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of AttemptService for UI testing.
 * Replace with real implementation when Person 4 completes their work.
 */
public class MockAttemptService implements AttemptService {
    
    private final Map<Integer, QuizAttempt> attempts = new HashMap<>();
    private int nextId = 1;
    
    public MockAttemptService() {
        initializeTestData();
    }
    
    private void initializeTestData() {
        // Create some sample attempts
        // Student 4 (Jane Doe) attempts
        addAttempt(createAttempt(1, 4, 85.0, List.of(
                new StudentAnswer(1, 'B', true),
                new StudentAnswer(2, 'B', true),
                new StudentAnswer(3, 'A', false)
        )));
        
        addAttempt(createAttempt(2, 4, 100.0, List.of(
                new StudentAnswer(4, 'A', true),
                new StudentAnswer(5, 'C', true)
        )));
        
        // Student 5 (Mike Brown) attempts
        addAttempt(createAttempt(1, 5, 66.7, List.of(
                new StudentAnswer(1, 'A', false),
                new StudentAnswer(2, 'B', true),
                new StudentAnswer(3, 'B', true)
        )));
        
        // Student 6 (Emily Davis) attempts
        addAttempt(createAttempt(1, 6, 100.0, List.of(
                new StudentAnswer(1, 'B', true),
                new StudentAnswer(2, 'B', true),
                new StudentAnswer(3, 'B', true)
        )));
        
        addAttempt(createAttempt(2, 6, 50.0, List.of(
                new StudentAnswer(4, 'A', true),
                new StudentAnswer(5, 'A', false)
        )));
    }
    
    private QuizAttempt createAttempt(int quizId, int studentId, double score, List<StudentAnswer> answers) {
        QuizAttempt attempt = new QuizAttempt(quizId, studentId, score, answers);
        // Set random time in past week
        attempt.setAttemptedAt(LocalDateTime.now().minusDays((long)(Math.random() * 7)));
        return attempt;
    }
    
    private void addAttempt(QuizAttempt attempt) {
        attempt.setId(nextId++);
        attempts.put(attempt.getId(), attempt);
    }
    
    @Override
    public QuizAttempt saveAttempt(QuizAttempt attempt) {
        attempt.setId(nextId++);
        attempts.put(attempt.getId(), attempt);
        return attempt;
    }
    
    @Override
    public List<QuizAttempt> getAttemptsByStudent(int studentId) {
        return attempts.values().stream()
                .filter(a -> a.getStudentId() == studentId)
                .sorted(Comparator.comparing(QuizAttempt::getAttemptedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<QuizAttempt> getAttemptsByQuiz(int quizId) {
        return attempts.values().stream()
                .filter(a -> a.getQuizId() == quizId)
                .sorted(Comparator.comparing(QuizAttempt::getAttemptedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<QuizAttempt> getAttemptById(int attemptId) {
        return Optional.ofNullable(attempts.get(attemptId));
    }
    
    @Override
    public List<QuizAttempt> getStudentAttemptsForQuiz(int studentId, int quizId) {
        return attempts.values().stream()
                .filter(a -> a.getStudentId() == studentId && a.getQuizId() == quizId)
                .sorted(Comparator.comparing(QuizAttempt::getAttemptedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean hasStudentAttemptedQuiz(int studentId, int quizId) {
        return attempts.values().stream()
                .anyMatch(a -> a.getStudentId() == studentId && a.getQuizId() == quizId);
    }
    
    @Override
    public Optional<Double> getBestScore(int studentId, int quizId) {
        return attempts.values().stream()
                .filter(a -> a.getStudentId() == studentId && a.getQuizId() == quizId)
                .map(QuizAttempt::getScore)
                .max(Double::compare);
    }
    
    @Override
    public double getAverageScoreForQuiz(int quizId) {
        List<QuizAttempt> quizAttempts = getAttemptsByQuiz(quizId);
        if (quizAttempts.isEmpty()) return 0.0;
        return quizAttempts.stream()
                .mapToDouble(QuizAttempt::getScore)
                .average()
                .orElse(0.0);
    }
    
    @Override
    public double getAverageScoreForStudent(int studentId) {
        List<QuizAttempt> studentAttempts = getAttemptsByStudent(studentId);
        if (studentAttempts.isEmpty()) return 0.0;
        return studentAttempts.stream()
                .mapToDouble(QuizAttempt::getScore)
                .average()
                .orElse(0.0);
    }
    
    @Override
    public int getTotalAttemptCount() {
        return attempts.size();
    }
    
    @Override
    public int getAttemptsToday() {
        LocalDate today = LocalDate.now();
        return (int) attempts.values().stream()
                .filter(a -> a.getAttemptedAt().toLocalDate().equals(today))
                .count();
    }
    
    @Override
    public Map<Integer, Double> getQuestionAccuracyForQuiz(int quizId) {
        Map<Integer, Integer> correctCount = new HashMap<>();
        Map<Integer, Integer> totalCount = new HashMap<>();
        
        for (QuizAttempt attempt : getAttemptsByQuiz(quizId)) {
            for (StudentAnswer answer : attempt.getAnswers()) {
                int qid = answer.getQuestionId();
                totalCount.merge(qid, 1, Integer::sum);
                if (answer.isCorrect()) {
                    correctCount.merge(qid, 1, Integer::sum);
                }
            }
        }
        
        Map<Integer, Double> accuracy = new HashMap<>();
        for (int qid : totalCount.keySet()) {
            int correct = correctCount.getOrDefault(qid, 0);
            int total = totalCount.get(qid);
            accuracy.put(qid, (correct * 100.0) / total);
        }
        
        return accuracy;
    }
    
    @Override
    public Map<String, Integer> getScoreDistribution(int quizId) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("0-20", 0);
        distribution.put("21-40", 0);
        distribution.put("41-60", 0);
        distribution.put("61-80", 0);
        distribution.put("81-100", 0);
        
        for (QuizAttempt attempt : getAttemptsByQuiz(quizId)) {
            double score = attempt.getScore();
            if (score <= 20) distribution.merge("0-20", 1, Integer::sum);
            else if (score <= 40) distribution.merge("21-40", 1, Integer::sum);
            else if (score <= 60) distribution.merge("41-60", 1, Integer::sum);
            else if (score <= 80) distribution.merge("61-80", 1, Integer::sum);
            else distribution.merge("81-100", 1, Integer::sum);
        }
        
        return distribution;
    }
}
