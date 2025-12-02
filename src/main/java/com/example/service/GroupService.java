package com.example.service;

import com.example.model.Group;
import com.example.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for group management operations.
 */
public interface GroupService {

    /**
     * Get all groups in the system.
     */
    List<Group> getAllGroups();

    /**
     * Get groups assigned to a specific teacher.
     */
    List<Group> getGroupsByTeacher(int teacherId);

    /**
     * Get a group by ID.
     */
    Optional<Group> getGroupById(int id);

    /**
     * Get a group by name.
     */
    Optional<Group> getGroupByName(String name);

    /**
     * Create a new group.
     * @return the created group with assigned ID
     */
    Group createGroup(Group group);

    /**
     * Update an existing group.
     */
    boolean updateGroup(Group group);

    /**
     * Delete a group by ID.
     */
    boolean deleteGroup(int id);

    /**
     * Get all students enrolled in a group.
     */
    List<User> getStudentsInGroup(int groupId);

    /**
     * Enroll a student in a group.
     */
    boolean enrollStudent(int groupId, int studentId);

    /**
     * Remove a student from a group.
     */
    boolean removeStudent(int groupId, int studentId);

    /**
     * Get all groups a student is enrolled in.
     */
    List<Group> getGroupsByStudent(int studentId);

    /**
     * Check if a student is enrolled in a group.
     */
    boolean isStudentEnrolled(int groupId, int studentId);
}
