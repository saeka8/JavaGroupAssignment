package com.example.quizlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizGrader {

    public static QuizAttempt gradeQuiz(
            int studentId,
            int quizId,
            List<Question> questions,
            Map<Integer, Character> studentAnswers
    ) {

        List<StudentAnswer> gradedAnswers = new ArrayList<>();
        int correctCount = 0;

        for (Question q : questions) {
            char selected = studentAnswers.getOrDefault(q.getId(), ' ');
            boolean isCorrect = (selected == q.getCorrectAnswer());

            if (isCorrect) {
                correctCount++;
            }

            gradedAnswers.add(
                new StudentAnswer(
                    q.getId(),
                    selected,
                    isCorrect
                )
            );
        }

        double score = (questions.isEmpty())
                ? 0.0
                : (correctCount * 100.0) / questions.size();

        return new QuizAttempt(
                quizId,
                studentId,
                score,
                gradedAnswers
        );
    }
}
