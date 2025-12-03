package com.example.quizlogic;

import java.util.*;
import java.util.stream.Collectors;

public class Analytics {
    //only contains static methods so it doesn't need to be instantiated
    public static List<Integer> getScoresOverTime(List<QuizAttempt> attempts) {
        return attempts.stream()
                .map(QuizAttempt::getTotalScore)
                .collect(Collectors.toList());
    }
    //the function above is used to get the scores from previous quizzes ordered by time

    public static double getCompletionRate(int totalAssignedQuizzes, List<QuizAttempt> attempts) {
        if (totalAssignedQuizzes == 0) {
            return 0.0;
        }

        Set<Integer> completedQuizIds = attempts.stream()
                .map(QuizAttempt::getQuizId)
                .collect(Collectors.toSet());

        return (completedQuizIds.size() * 100.0) / totalAssignedQuizzes;
    }

    public static Map<Integer, Double> getQuestionAccuracy(List<QuizAttempt> attempts) {

        Map<Integer, Integer> correctCount = new HashMap<>();
        Map<Integer, Integer> totalCount   = new HashMap<>();

        for (QuizAttempt attempt : attempts) {
            for (StudentAnswer answer : attempt.getAnswers()) {

                int qid = answer.getQuestionId();

                totalCount.put(qid, totalCount.getOrDefault(qid, 0) + 1);

                if (answer.isCorrect()) {
                    correctCount.put(qid, correctCount.getOrDefault(qid, 0) + 1);
                }
            }
        }

        Map<Integer, Double> accuracy = new HashMap<>();

        for (int qid : totalCount.keySet()) {
            int correct = correctCount.getOrDefault(qid, 0);
            int total   = totalCount.get(qid);

            double percent = (total == 0) ? 0.0 : (correct * 100.0) / total;

            accuracy.put(qid, percent);
        }

        return accuracy;
    }

    public static Map<Integer, List<QuizAttempt>> groupAttemptsByQuiz(List<QuizAttempt> attempts) {
        return attempts.stream()
                .collect(Collectors.groupingBy(QuizAttempt::getQuizId));
    }

    /**
     * Convert raw scores to percentages based on total questions
     */
    public static List<Double> convertToPercentages(List<Integer> scores, int totalQuestions) {
        if (totalQuestions == 0) return Collections.emptyList();
        
        return scores.stream()
                .map(score -> (score * 100.0) / totalQuestions)
                .collect(Collectors.toList());
    }
}
