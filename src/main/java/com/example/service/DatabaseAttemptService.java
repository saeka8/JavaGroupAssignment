package com.example.service;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;
import com.example.database.RetrieveFromDatabase;
import com.example.quizlogic.QuizAttempt;
import com.example.quizlogic.StudentAnswer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Real implementation of AttemptService using SQLite database.
 */
public class DatabaseAttemptService implements AttemptService {

    private final Connection conn;

    public DatabaseAttemptService() {
        this.conn = DatabaseManager.connectWithDatabase();
        if (conn == null) {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    @Override
    public QuizAttempt saveAttempt(QuizAttempt attempt) {
        try {
            // Get the next attempt number
            int attemptNumber = RetrieveFromDatabase.getNextAttemptNumber(conn, attempt.getQuizId(), attempt.getStudentId());

            // Save the overall score
            InsertIntoDatabase.insertScore(conn, attempt.getQuizId(), attempt.getStudentId(), attemptNumber, attempt.getTotalScore());

            // Save each answer
            for (StudentAnswer answer : attempt.getAnswers()) {
                InsertIntoDatabase.insertStudentAnswer(
                    conn,
                    answer.getQuestionId(),
                    attempt.getStudentId(),
                    attemptNumber,
                    answer.getSelectedOption()
                );
            }

            return attempt;
        } catch (SQLException e) {
            System.err.println("Error saving attempt: " + e.getMessage());
            return attempt;
        }
    }

    @Override
    public List<QuizAttempt> getAttemptsByStudent(int studentId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT quiz_id, student_id, attempt, score FROM scores WHERE student_id=" + studentId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int quizId = rs.getInt("quiz_id");
                int attemptNum = rs.getInt("attempt");
                int score = rs.getInt("score");

                List<StudentAnswer> answers = getAnswersForAttempt(quizId, studentId, attemptNum);
                QuizAttempt attempt = new QuizAttempt(quizId, studentId, attemptNum, score, answers);
                attempts.add(attempt);
            }
        } catch (SQLException e) {
            System.err.println("Error getting attempts by student: " + e.getMessage());
        }

        return attempts;
    }

    @Override
    public List<QuizAttempt> getAttemptsByQuiz(int quizId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT quiz_id, student_id, attempt, score FROM scores WHERE quiz_id=" + quizId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                int attemptNum = rs.getInt("attempt");
                int score = rs.getInt("score");

                List<StudentAnswer> answers = getAnswersForAttempt(quizId, studentId, attemptNum);
                QuizAttempt attempt = new QuizAttempt(quizId, studentId, attemptNum, score, answers);
                attempts.add(attempt);
            }
        } catch (SQLException e) {
            System.err.println("Error getting attempts by quiz: " + e.getMessage());
        }

        return attempts;
    }

    @Override
    public Optional<QuizAttempt> getAttemptById(int attemptId) {
        // Note: Your schema doesn't have attempt ID as primary key
        // Using quiz_id + student_id + attempt number combination instead
        return Optional.empty();
    }

    @Override
    public List<QuizAttempt> getStudentAttemptsForQuiz(int studentId, int quizId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT quiz_id, student_id, attempt, score FROM scores " +
                     "WHERE student_id=" + studentId + " AND quiz_id=" + quizId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int attemptNum = rs.getInt("attempt");
                int score = rs.getInt("score");

                List<StudentAnswer> answers = getAnswersForAttempt(quizId, studentId, attemptNum);
                QuizAttempt attempt = new QuizAttempt(quizId, studentId, attemptNum, score, answers);
                attempts.add(attempt);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student attempts for quiz: " + e.getMessage());
        }

        return attempts;
    }

    @Override
    public boolean hasStudentAttemptedQuiz(int studentId, int quizId) {
        String sql = "SELECT COUNT(*) as count FROM scores WHERE student_id=" + studentId + " AND quiz_id=" + quizId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if student attempted quiz: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int getTotalAttemptCount() {
        String sql = "SELECT COUNT(*) as count FROM scores";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total attempt count: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int getAttemptsToday() {
        String today = LocalDate.now().toString();
        String sql = "SELECT COUNT(*) as count FROM mcqStudentAnswer WHERE date='" + today + "'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting attempts today: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public Map<Integer, Double> getQuestionAccuracyForQuiz(int quizId) {
        Map<Integer, Double> accuracyMap = new HashMap<>();

        // Get all questions for the quiz
        String sql = "SELECT qq.question_id, " +
                     "SUM(CASE WHEN sa.is_correct = 1 THEN 1 ELSE 0 END) as correct_count, " +
                     "COUNT(*) as total_count " +
                     "FROM quizQuestion qq " +
                     "LEFT JOIN mcqStudentAnswer sa ON qq.question_id = sa.question_id " +
                     "WHERE qq.quiz_id=" + quizId + " " +
                     "GROUP BY qq.question_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                int correctCount = rs.getInt("correct_count");
                int totalCount = rs.getInt("total_count");

                double accuracy = totalCount > 0 ? (correctCount * 100.0 / totalCount) : 0.0;
                accuracyMap.put(questionId, accuracy);
            }
        } catch (SQLException e) {
            System.err.println("Error getting question accuracy: " + e.getMessage());
        }

        return accuracyMap;
    }

    @Override
    public Map<String, Integer> getScoreDistribution(int quizId) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("0-20", 0);
        distribution.put("21-40", 0);
        distribution.put("41-60", 0);
        distribution.put("61-80", 0);
        distribution.put("81-100", 0);

        String sql = "SELECT score FROM scores WHERE quiz_id=" + quizId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int score = rs.getInt("score");

                // Categorize score
                String range;
                if (score <= 20) range = "0-20";
                else if (score <= 40) range = "21-40";
                else if (score <= 60) range = "41-60";
                else if (score <= 80) range = "61-80";
                else range = "81-100";

                distribution.put(range, distribution.get(range) + 1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting score distribution: " + e.getMessage());
        }

        return distribution;
    }

    // Helper method to get answers for a specific attempt
    private List<StudentAnswer> getAnswersForAttempt(int quizId, int studentId, int attemptNum) {
        List<StudentAnswer> answers = new ArrayList<>();
        String sql = "SELECT sa.question_id, sa.selected_option, sa.is_correct, sa.score, sa.attempt, m.assigned_score " +
                     "FROM mcqStudentAnswer sa " +
                     "INNER JOIN quizQuestion qq ON sa.question_id = qq.question_id " +
                     "INNER JOIN mcq m ON sa.question_id = m.id " +
                     "WHERE qq.quiz_id=" + quizId + " AND sa.student_id=" + studentId +
                     " AND sa.attempt=" + attemptNum;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                char selectedOption = rs.getString("selected_option").charAt(0);
                boolean isCorrect = rs.getBoolean("is_correct");
                int assignedScore = rs.getInt("assigned_score");
                int scoreEarned = rs.getInt("score");
                int attempt = rs.getInt("attempt");

                StudentAnswer answer = new StudentAnswer(questionId, selectedOption, isCorrect, assignedScore, scoreEarned, attempt);
                answers.add(answer);
            }
        } catch (SQLException e) {
            System.err.println("Error getting answers for attempt: " + e.getMessage());
        }

        return answers;
    }
}
