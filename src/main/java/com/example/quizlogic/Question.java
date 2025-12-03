package com.example.quizlogic;

public class Question {
    private int id; // question ID
    private String text; // question text

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private char correctOption; // stores the correct option in the form of A, B, C or D
    private int assignedScore;

    public Question(int id, String text, String optionA, String optionB, String optionC, String optionD,
            char correctOption, int assignedScore) {
        this.id = id;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.assignedScore = assignedScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public char getCorrectAnswer() {
        return correctOption;
    }

    public void setCorrectAnswer(char correctOption) {
        this.correctOption = correctOption;
    }

    public int getAssignedScore() {
        return assignedScore;
    }

    public void setAssignedScore(int assignedScore) {
        this.assignedScore = assignedScore;
    }

    /**
     * Returns the text of a specific option.
     * 
     * @param option 'A', 'B', 'C', or 'D'
     * @return the option text, or null if invalid
     */
    public String getOptionText(char option) {
        return switch (Character.toUpperCase(option)) {
            case 'A' -> optionA;
            case 'B' -> optionB;
            case 'C' -> optionC;
            case 'D' -> optionD;
            default -> null;
        };
    }
}