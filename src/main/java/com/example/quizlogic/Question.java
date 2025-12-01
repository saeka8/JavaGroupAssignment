package com.example.quizlogic;
public class Question {
    private final int id; //question ID
    private final String text; //question text

    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;

    private final char correctOption; //stores the correct option in the form of A, B, C or D
    private final int assignedScore;

    public Question(int id, String text, String optionA, String optionB, String optionC, String optionD, char correctOption, int assignedScore) {
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
    public String getText() {
        return text;
    }
    public String getOptionA() {
        return optionA;
    }
    public String getOptionB() {
        return optionB;
    }
    public String getOptionC() {
        return optionC;
    }
    public String getOptionD() {
        return optionD;
    }
    
    public char getCorrectAnswer() {
        return correctOption;
    }
    public int getAssignedScore() {
        return assignedScore;
    }
    
    /**
     * Returns the text of a specific option.
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