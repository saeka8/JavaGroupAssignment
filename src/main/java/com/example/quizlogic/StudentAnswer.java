package com.example.quizlogic;

public class StudentAnswer {
    private final int questionId;
    private final char selectedOption;
    private final boolean isCorrect;

    public StudentAnswer(int questionId, char selectedOption, boolean isCorrect) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
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