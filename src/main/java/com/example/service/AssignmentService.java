package com.example.service;

import com.example.model.Quiz;
import com.example.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for quiz assignment operations.
 *
 * This interface defines operations for:
 * - Assigning quizzes to individual students or groups
 * - Retrieving assignment information
 * - Managing assignment lifecycle (create, delete)
 */
public interface AssignmentService {

    /**
     * Assigns a quiz to a single student.
     *
     * @param quizId the quiz to assign
     * @param studentId the student to assign the quiz to
     * @return true if assignment was successful, false otherwise
     * @throws IllegalArgumentException if quiz or student doesn't exist
     */
    boolean assignQuiz(int quizId, int studentId);

    /**
     * Assigns a quiz to multiple students at once.
     *
     * @param quizId the quiz to assign
     * @param studentIds list of student IDs to assign the quiz to
     * @return number of successful assignments
     * @throws IllegalArgumentException if quiz doesn't exist
     */
    int assignQuizToMultipleStudents(int quizId, List<Integer> studentIds);

    /**
     * Removes a quiz assignment from a student.
     *
     * @param quizId the quiz ID
     * @param studentId the student ID
     * @return true if unassignment was successful
     */
    boolean unassignQuiz(int quizId, int studentId);

    /**
     * Removes all assignments for a specific quiz.
     * Typically used when deleting a quiz.
     *
     * @param quizId the quiz ID
     * @return number of assignments removed
     */
    int unassignAllFromQuiz(int quizId);

    /**
     * Gets all quizzes assigned to a specific student.
     *
     * @param studentId the student ID
     * @return list of assigned quizzes (may be empty)
     */
    List<Quiz> getQuizzesAssignedToStudent(int studentId);

    /**
     * Gets all students assigned to a specific quiz.
     *
     * @param quizId the quiz ID
     * @return list of assigned students (may be empty)
     */
    List<User> getStudentsAssignedToQuiz(int quizId);

    /**
     * Checks if a specific quiz is assigned to a specific student.
     *
     * @param quizId the quiz ID
     * @param studentId the student ID
     * @return true if assigned, false otherwise
     */
    boolean isAssigned(int quizId, int studentId);

    /**
     * Gets the assignment date for a specific quiz-student pair.
     *
     * @param quizId the quiz ID
     * @param studentId the student ID
     * @return the assignment date, or null if not assigned
     */
    LocalDateTime getAssignmentDate(int quizId, int studentId);

    /**
     * Gets the count of students assigned to a quiz.
     *
     * @param quizId the quiz ID
     * @return number of students assigned
     */
    int getAssignedStudentCount(int quizId);

    /**
     * Gets the count of quizzes assigned to a student.
     *
     * @param studentId the student ID
     * @return number of quizzes assigned
     */
    int getAssignedQuizCount(int studentId);

    /**
     * Gets all students who have NOT been assigned a specific quiz.
     * Useful for the assignment UI to show available students.
     *
     * @param quizId the quiz ID
     * @return list of students not yet assigned to this quiz
     */
    List<User> getUnassignedStudents(int quizId);
}