package com.example.quizlogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizTakingSession {

    private final List<Question> questions;
    private final Map<Integer, Character> answers; 
    private int currentIndex;

    public QuizTakingSession(List<Question> questions) {
        this.questions = questions;
        this.answers = new HashMap<>();
        this.currentIndex = 0;
    }

    public Question getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public boolean hasNext() {
        return currentIndex < questions.size() - 1;
    }

    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    public void next() {
        if (hasNext()) {
            currentIndex++;
        }
    }

    public void previous() {
        if (hasPrevious()) {
            currentIndex--;
        }
    }

    public void answerCurrentQuestion(char answer) {
        answers.put(getCurrentQuestion().getId(), Character.toUpperCase(answer));
    }

    public Character getAnswerForCurrentQuestion() {
        return answers.get(getCurrentQuestion().getId());
    }

    public Map<Integer, Character> getAllAnswers() {
        return answers;
    }

    public boolean isComplete() {
        return answers.size() == questions.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }
}
