package com.example.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;
import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;

/**
 * Real implementation of QuizService using SQLite database.
 */
public class DatabaseQuizService implements QuizService {

    private final Connection conn;
    private final UserService userService;

    public DatabaseQuizService(UserService userService) {
        this.conn = DatabaseManager.connectWithDatabase();
        if (conn == null) {
            throw new RuntimeException("Failed to connect to database");
        }
        this.userService = userService;
    }

    @Override
    public Quiz createQuiz(Quiz quiz) {
        try {
            // Insert quiz using the group id set on the Quiz object (teacher is derived from the group)
            int quizId = InsertIntoDatabase.insertQuiz(conn, quiz.getTitle(), quiz.getDescription(), quiz.getGroupId());
            quiz.setId(quizId);
            return quiz;
        } catch (SQLException e) {
            System.err.println("Error creating quiz: " + e.getMessage());
            return quiz;
        }
    }

    @Override
    public boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quiz SET quiz_name='" + quiz.getTitle() + "', description='" + quiz.getDescription() +
                     "' WHERE id=" + quiz.getId();

        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating quiz: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteQuiz(int quizId) {
        try {
            // Delete related records first
            String deleteQuestions = "DELETE FROM quizQuestion WHERE quiz_id=" + quizId;
            String deleteScores = "DELETE FROM scores WHERE quiz_id=" + quizId;
            String deleteQuiz = "DELETE FROM quiz WHERE id=" + quizId;

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteQuestions);
                stmt.executeUpdate(deleteScores);
                int rowsAffected = stmt.executeUpdate(deleteQuiz);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Quiz> getQuizById(int quizId) {
        String sql = "SELECT q.id, q.quiz_name, q.description, q.group_id, g.teacher_id " +
                     "FROM quiz q LEFT JOIN groups g ON q.group_id = g.id WHERE q.id=" + quizId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Quiz quiz = extractQuizFromResultSet(rs);
                quiz.setQuestions(getQuestionsByQuiz(quizId));
                return Optional.of(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error getting quiz by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.id, q.quiz_name, q.description, q.group_id, g.teacher_id " +
                     "FROM quiz q LEFT JOIN groups g ON q.group_id = g.id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Quiz quiz = extractQuizFromResultSet(rs);
                quiz.setQuestions(getQuestionsByQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all quizzes: " + e.getMessage());
        }

        return quizzes;
    }

    @Override
    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.id, q.quiz_name, q.description, q.group_id, g.teacher_id " +
                     "FROM quiz q LEFT JOIN groups g ON q.group_id = g.id WHERE g.teacher_id=" + teacherId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Quiz quiz = extractQuizFromResultSet(rs);
                quiz.setQuestions(getQuestionsByQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error getting quizzes by teacher: " + e.getMessage());
        }

        return quizzes;
    }

    @Override
    public List<Quiz> getQuizzesByGroup(int groupId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.id, q.quiz_name, q.description, q.group_id, g.teacher_id " +
                     "FROM quiz q LEFT JOIN groups g ON q.group_id = g.id WHERE q.group_id=" + groupId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Quiz quiz = extractQuizFromResultSet(rs);
                quiz.setQuestions(getQuestionsByQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error getting quizzes by group: " + e.getMessage());
        }

        return quizzes;
    }

    @Override
    public List<Quiz> searchQuizzes(String query) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.id, q.quiz_name, q.description, q.group_id, g.teacher_id " +
                     "FROM quiz q LEFT JOIN groups g ON q.group_id = g.id " +
                     "WHERE q.quiz_name LIKE '%" + query + "%' OR q.description LIKE '%" + query + "%'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Quiz quiz = extractQuizFromResultSet(rs);
                quiz.setQuestions(getQuestionsByQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error searching quizzes: " + e.getMessage());
        }

        return quizzes;
    }

    @Override
    public Question addQuestion(int quizId, Question question) {
        try {
            // Insert the MCQ
            int questionId = InsertIntoDatabase.insertMcq(
                conn,
                question.getText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectAnswer(),
                question.getAssignedScore()
            );

            // Link it to the quiz
            InsertIntoDatabase.insertQuizQuestion(conn, quizId, questionId);

            // Return new Question object with ID
            return new Question(
                questionId,
                question.getText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectAnswer(),
                question.getAssignedScore()
            );
        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            return question;
        }
    }

    @Override
    public boolean updateQuestion(Question question) {
        String sql = "UPDATE mcq SET question='" + question.getText() +
                     "', optionA='" + question.getOptionA() +
                     "', optionB='" + question.getOptionB() +
                     "', optionC='" + question.getOptionC() +
                     "', optionD='" + question.getOptionD() +
                     "', correct_option='" + question.getCorrectAnswer() +
                     "', assigned_score=" + question.getAssignedScore() +
                     " WHERE id=" + question.getId();

        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteQuestion(int questionId) {
        try {
            // Delete from linking table first
            String deleteLink = "DELETE FROM quizQuestion WHERE question_id=" + questionId;
            String deleteQuestion = "DELETE FROM mcq WHERE id=" + questionId;

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteLink);
                int rowsAffected = stmt.executeUpdate(deleteQuestion);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Question> getQuestionsByQuiz(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT m.id, m.question, m.optionA, m.optionB, m.optionC, m.optionD, m.correct_option, m.assigned_score " +
                     "FROM mcq m INNER JOIN quizQuestion qq ON m.id = qq.question_id WHERE qq.quiz_id=" + quizId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                questions.add(extractQuestionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting questions by quiz: " + e.getMessage());
        }

        return questions;
    }

    @Override
    public boolean assignQuizToStudent(int quizId, int studentId) {
        // In your schema, this would use the enrollment table
        // For now, we'll use a simple approach - you may need to adjust based on your group logic
        try {
            // This is a placeholder - you'll need to implement based on your group/enrollment logic
            System.out.println("Assigning quiz " + quizId + " to student " + studentId);
            return true;
        } catch (Exception e) {
            System.err.println("Error assigning quiz: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean assignQuizToStudents(int quizId, List<Integer> studentIds) {
        boolean allSuccess = true;
        for (int studentId : studentIds) {
            if (!assignQuizToStudent(quizId, studentId)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    @Override
    public List<Quiz> getAssignedQuizzes(int studentId) {
        // Get quizzes from groups the student is enrolled in
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT DISTINCT q.id, q.quiz_name, q.description, q.group_id, g.teacher_id " +
                     "FROM quiz q " +
                     "INNER JOIN groups g ON q.group_id = g.id " +
                     "INNER JOIN enrollment e ON g.id = e.group_id " +
                     "WHERE e.student_id=" + studentId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Quiz quiz = extractQuizFromResultSet(rs);
                quiz.setQuestions(getQuestionsByQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error getting assigned quizzes: " + e.getMessage());
        }

        return quizzes;
    }

    @Override
    public List<User> getStudentsAssignedToQuiz(int quizId) {
        List<User> students = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.name, p.lastname, p.email, p.password, p.role " +
                     "FROM people p " +
                     "INNER JOIN enrollment e ON p.id = e.student_id " +
                     "INNER JOIN groups g ON e.group_id = g.id " +
                     "INNER JOIN quiz q ON g.id = q.group_id " +
                     "WHERE q.id=" + quizId + " AND p.role='student'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students assigned to quiz: " + e.getMessage());
        }

        return students;
    }

    @Override
    public boolean isQuizAssigned(int quizId, int studentId) {
        String sql = "SELECT COUNT(*) as count " +
                     "FROM enrollment e " +
                     "INNER JOIN groups g ON e.group_id = g.id " +
                     "INNER JOIN quiz q ON g.id = q.group_id " +
                     "WHERE q.id=" + quizId + " AND e.student_id=" + studentId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking quiz assignment: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int getTotalQuizCount() {
        String sql = "SELECT COUNT(*) as count FROM quiz";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total quiz count: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int getActiveQuizCount() {
        // All quizzes are active for now (no isActive flag in DB schema)
        return getTotalQuizCount();
    }

    // Helper methods
    private Quiz extractQuizFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("quiz_name");
        String description = rs.getString("description");
        int groupId = rs.getInt("group_id");
        int teacherId = rs.getInt("teacher_id");

        Quiz quiz = new Quiz(id, title, description, teacherId);
        // Ensure the quiz object knows which group it belongs to
        quiz.setGroupId(groupId);

        // Get teacher name if available
        Optional<User> teacher = userService.getUserById(teacherId);
        teacher.ifPresent(t -> quiz.setTeacherName(t.getFirstName() + " " + t.getLastName()));

        return quiz;
    }

    @Override
    public boolean addQuestionToQuiz(int quizId, String question, String optionA, String optionB,
                                     String optionC, String optionD, char correctOption, int score) {
        try {
            // Insert the MCQ
            int questionId = InsertIntoDatabase.insertMcq(
                conn, question, optionA, optionB, optionC, optionD, correctOption, score
            );

            // Link question to quiz
            InsertIntoDatabase.insertQuizQuestion(conn, quizId, questionId);

            return true;
        } catch (SQLException e) {
            System.err.println("Error adding question to quiz: " + e.getMessage());
            return false;
        }
    }

    private Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("id"),
            rs.getString("question"),
            rs.getString("optionA"),
            rs.getString("optionB"),
            rs.getString("optionC"),
            rs.getString("optionD"),
            rs.getString("correct_option").charAt(0),
            rs.getInt("assigned_score")
        );
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String lastname = rs.getString("lastname");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");

        User.Role role;
        if ("admin".equalsIgnoreCase(roleStr)) {
            role = User.Role.ADMIN;
        } else if ("teacher".equalsIgnoreCase(roleStr)) {
            role = User.Role.TEACHER;
        } else {
            role = User.Role.STUDENT;
        }

        return User.createUser(id, email, password, name, lastname, role);
    }
}
