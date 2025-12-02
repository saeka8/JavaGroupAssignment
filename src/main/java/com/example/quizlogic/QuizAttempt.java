package com.example.quizlogic;

import java.time.LocalDateTime;
import java.util.List;

public class QuizAttempt {
    private int id;
    private final int quizId;
    private final int studentId;
    private final int attemptNumber;
    private final int totalScore;
    private final List<StudentAnswer> answers;
    private LocalDateTime attemptedAt;

    public QuizAttempt(int quizId, int studentId, int attemptNumber, int totalScore, List<StudentAnswer> answers) {
        this.quizId = quizId;
        this.studentId = studentId;
        this.attemptNumber = attemptNumber;
        this.totalScore = totalScore;
        this.answers = answers;
        this.attemptedAt = LocalDateTime.now();
    }
    
    // Old constructor for compatibility (uses attemptNumber = 1)
    public QuizAttempt(int quizId, int studentId, int score, List<StudentAnswer> answers) {
        this(quizId, studentId, 1, score, answers);
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }
    
    public int getQuizId() {
        return quizId;
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    // Alias for compatibility
    public double getScore() {
        return totalScore;
    }
    
    public List<StudentAnswer> getAnswers() {
        return answers;
    }
    
    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }
    
    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}
