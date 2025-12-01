package com.example.quizlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizGrader {

    public static QuizAttempt gradeQuiz(
            int studentId,
            int quizId,
            int attemptNumber,
            List<Question> questions,
            Map<Integer, Character> studentAnswers
    ) {

        List<StudentAnswer> gradedAnswers = new ArrayList<>();
        int totalScore = 0;

        for (Question q : questions) {
            char selected = studentAnswers.getOrDefault(q.getId(), ' ');
            boolean isCorrect = (selected == q.getCorrectAnswer());

            int assignedScore = q.getAssignedScore();

            int scoreEarned = isCorrect ? assignedScore : 0;

            totalScore += scoreEarned;

            gradedAnswers.add(
                new StudentAnswer(
                    q.getId(),
                    selected,
                    isCorrect,
                    assignedScore,
                    scoreEarned,
                    attemptNumber
                )
            );
        }

        return new QuizAttempt(
                quizId,
                studentId,
                attemptNumber,
                totalScore,
                gradedAnswers
        );
    }
}
