package com.example.quizlogic;
import java.util.List;

public class QuizAttempt {
    private final int quizId;
    private final int studentId;
    private final double score;
    private final List<StudentAnswer> answers;

    public QuizAttempt(int studentId, int quizId, List<StudentAnswer> answers) {
        this.studentId = studentId;
        this.quizId = quizId;
        this.answers = answers;
    }

    public int getStudentId() {
        return studentId;
    }   
    public int getQuizId() {
        return quizId;
    }
    public double getScore(){
        return score;
    }   
    public List<StudentAnswer> getAnswers() {
        return answers;
    }
}