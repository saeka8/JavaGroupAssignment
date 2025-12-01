package com.example.quizlogic;

import java.time.LocalDateTime;
import java.util.List;

public class QuizAttempt {
    private int id;
    private final int quizId;
    private final int studentId;
    private final double score;
    private final List<StudentAnswer> answers;
    private LocalDateTime attemptedAt;

    public QuizAttempt(int quizId, int studentId, double score, List<StudentAnswer> answers) {
        this.quizId = quizId;
        this.studentId = studentId;
        this.score = score;
        this.answers = answers;
        this.attemptedAt = LocalDateTime.now();
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
    
    public double getScore() {
        return score;
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