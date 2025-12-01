package com.example.service.impl;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.service.AssignmentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Real implementation of AssignmentService.
 *
 * Handles all quiz assignment operations including:
 * - Assigning quizzes to students (individually or in bulk)
 * - Unassigning quizzes
 * - Querying assignment information
 *
 * TODO: Replace placeholder implementations with actual DAO calls
 * once Person 1 completes the database layer.
 */
public class AssignmentServiceImpl implements AssignmentService {

    // TODO: Inject these DAOs when Person 1 completes them
    // private final AssignmentDao assignmentDao;
    // private final QuizDao quizDao;
    // private final UserDao userDao;

    public AssignmentServiceImpl() {
        // TODO: Initialize DAOs here
        // this.assignmentDao = new AssignmentDao();
        // this.quizDao = new QuizDao();
        // this.userDao = new UserDao();
    }

    @Override
    public boolean assignQuiz(int quizId, int studentId) {
        // Validate inputs
        if (quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }
        if (studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }

        // TODO: Validate quiz exists
        // Quiz quiz = quizDao.findById(quizId);
        // if (quiz == null) {
        //     throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        // }

        // TODO: Validate student exists and has STUDENT role
        // User student = userDao.findById(studentId);
        // if (student == null) {
        //     throw new IllegalArgumentException("Student not found with ID: " + studentId);
        // }
        // if (student.getRole() != User.Role.STUDENT) {
        //     throw new IllegalArgumentException("User is not a student");
        // }

        // Check if already assigned (idempotent operation)
        if (isAssigned(quizId, studentId)) {
            return true; // Already assigned
        }

        // TODO: Create assignment record
        // Assignment assignment = new Assignment();
        // assignment.setQuizId(quizId);
        // assignment.setStudentId(studentId);
        // assignment.setAssignedDate(LocalDateTime.now());
        // return assignmentDao.insert(assignment) != null;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public int assignQuizToMultipleStudents(int quizId, List<Integer> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return 0;
        }

        // TODO: Validate quiz exists once
        // Quiz quiz = quizDao.findById(quizId);
        // if (quiz == null) {
        //     throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        // }

        int successCount = 0;
        for (Integer studentId : studentIds) {
            try {
                if (assignQuiz(quizId, studentId)) {
                    successCount++;
                }
            } catch (Exception e) {
                // Log error but continue with other students
                System.err.println("Failed to assign quiz " + quizId +
                        " to student " + studentId + ": " + e.getMessage());
            }
        }
        return successCount;
    }

    @Override
    public boolean unassignQuiz(int quizId, int studentId) {
        if (quizId <= 0 || studentId <= 0) {
            return false;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.delete(quizId, studentId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public int unassignAllFromQuiz(int quizId) {
        if (quizId <= 0) {
            return 0;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.deleteAllByQuizId(quizId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<Quiz> getQuizzesAssignedToStudent(int studentId) {
        if (studentId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // List<Assignment> assignments = assignmentDao.findByStudentId(studentId);
        // List<Quiz> quizzes = new ArrayList<>();
        // for (Assignment assignment : assignments) {
        //     Quiz quiz = quizDao.findById(assignment.getQuizId());
        //     if (quiz != null) {
        //         quizzes.add(quiz);
        //     }
        // }
        // return quizzes;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<User> getStudentsAssignedToQuiz(int quizId) {
        if (quizId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // List<Assignment> assignments = assignmentDao.findByQuizId(quizId);
        // List<User> students = new ArrayList<>();
        // for (Assignment assignment : assignments) {
        //     User student = userDao.findById(assignment.getStudentId());
        //     if (student != null) {
        //         students.add(student);
        //     }
        // }
        // return students;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public boolean isAssigned(int quizId, int studentId) {
        if (quizId <= 0 || studentId <= 0) {
            return false;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.exists(quizId, studentId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public LocalDateTime getAssignmentDate(int quizId, int studentId) {
        if (quizId <= 0 || studentId <= 0) {
            return null;
        }

        // TODO: Replace with actual DAO call
        // Assignment assignment = assignmentDao.findByQuizAndStudent(quizId, studentId);
        // return assignment != null ? assignment.getAssignedDate() : null;

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public int getAssignedStudentCount(int quizId) {
        if (quizId <= 0) {
            return 0;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.countByQuizId(quizId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public int getAssignedQuizCount(int studentId) {
        if (studentId <= 0) {
            return 0;
        }

        // TODO: Replace with actual DAO call
        // return assignmentDao.countByStudentId(studentId);

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }

    @Override
    public List<User> getUnassignedStudents(int quizId) {
        if (quizId <= 0) {
            return new ArrayList<>();
        }

        // TODO: Replace with actual DAO call
        // Get all students
        // List<User> allStudents = userDao.findByRole(User.Role.STUDENT);

        // Get assigned student IDs
        // List<Assignment> assignments = assignmentDao.findByQuizId(quizId);
        // Set<Integer> assignedIds = assignments.stream()
        //         .map(Assignment::getStudentId)
        //         .collect(Collectors.toSet());

        // Filter out assigned students
        // return allStudents.stream()
        //         .filter(s -> !assignedIds.contains(s.getId()))
        //         .collect(Collectors.toList());

        // Placeholder
        throw new UnsupportedOperationException("DAO not implemented yet");
    }
}