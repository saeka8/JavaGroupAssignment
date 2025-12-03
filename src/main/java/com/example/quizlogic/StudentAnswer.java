package com.example.quizlogic;

public class StudentAnswer {
    private final int questionId;
    private final char selectedOption;
    private final boolean isCorrect;
    private final int assignedScore;
    private final int scoreEarned;
    private final int attemptNumber;

    public StudentAnswer(int questionId, char selectedOption, boolean isCorrect, int assignedScore, int scoreEarned, int attemptNumber) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.assignedScore = assignedScore;
        this.scoreEarned = scoreEarned;
        this.attemptNumber = attemptNumber;
    }

    public int getQuestionId() {
        return questionId;
    }   
    public char getSelectedOption() {
        return selectedOption;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
}