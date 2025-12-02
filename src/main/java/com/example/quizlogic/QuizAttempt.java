package com.example.quizlogic;

import java.time.LocalDateTime;
import java.util.List;

public class QuizAttempt {
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

    public int getStudentId() {
        return studentId;
    }
    
    public int getQuizId() {
        return quizId;
    }
    
    public int getTotalScore() {
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