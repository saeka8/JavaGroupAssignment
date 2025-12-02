package com.example.service;

import com.example.model.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DatabaseUserService - verifies user management and persistence.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseUserServiceTest {

    private static DatabaseUserService userService;
    private static DatabaseAuthService authService;
    private static int adminId;
    private static int teacherId;
    private static int studentId;

    @BeforeAll
    static void setupService() {
        // Use the same test database created by auth tests
        authService = new DatabaseAuthService();
        userService = new DatabaseUserService();
        System.out.println("✓ DatabaseUserService test initialized");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Get all users")
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();

        assertNotNull(users, "User list should not be null");
        assertTrue(users.size() >= 3, "Should have at least 3 users (admin, teacher, student)");
        System.out.println("✓ Retrieved " + users.size() + " users from database");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Get users by role - ADMIN")
    void testGetUsersByRoleAdmin() {
        List<User> admins = userService.getUsersByRole(User.Role.ADMIN);

        assertNotNull(admins, "Admin list should not be null");
        assertTrue(admins.size() >= 1, "Should have at least 1 admin");
        assertTrue(admins.stream().allMatch(u -> u.getRole() == User.Role.ADMIN),
                "All users should have ADMIN role");
        System.out.println("✓ Retrieved " + admins.size() + " admin(s)");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Get users by role - TEACHER")
    void testGetUsersByRoleTeacher() {
        List<User> teachers = userService.getUsersByRole(User.Role.TEACHER);

        assertNotNull(teachers, "Teacher list should not be null");
        assertTrue(teachers.size() >= 1, "Should have at least 1 teacher");
        assertTrue(teachers.stream().allMatch(u -> u.getRole() == User.Role.TEACHER),
                "All users should have TEACHER role");
        System.out.println("✓ Retrieved " + teachers.size() + " teacher(s)");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Get users by role - STUDENT")
    void testGetUsersByRoleStudent() {
        List<User> students = userService.getUsersByRole(User.Role.STUDENT);

        assertNotNull(students, "Student list should not be null");
        assertTrue(students.size() >= 1, "Should have at least 1 student");
        assertTrue(students.stream().allMatch(u -> u.getRole() == User.Role.STUDENT),
                "All users should have STUDENT role");
        System.out.println("✓ Retrieved " + students.size() + " student(s)");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Get user by email")
    void testGetUserByEmail() {
        Optional<User> admin = userService.getUserByEmail("admin@test.com");

        assertTrue(admin.isPresent(), "Admin should be found by email");
        assertEquals("Admin", admin.get().getFirstName());
        assertEquals("User", admin.get().getLastName());
        assertEquals(User.Role.ADMIN, admin.get().getRole());

        adminId = admin.get().getId();
        System.out.println("✓ Found admin by email (ID: " + adminId + ")");
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Get user by ID")
    void testGetUserById() {
        Optional<User> teacher = userService.getUserByEmail("teacher@test.com");
        assertTrue(teacher.isPresent(), "Teacher should exist");
        teacherId = teacher.get().getId();

        Optional<User> foundById = userService.getUserById(teacherId);
        assertTrue(foundById.isPresent(), "Teacher should be found by ID");
        assertEquals("teacher@test.com", foundById.get().getEmail());
        assertEquals("John", foundById.get().getFirstName());
        System.out.println("✓ Found teacher by ID (ID: " + teacherId + ")");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Create new user")
    void testCreateUser() {
        User newUser = new User("bob@test.com", "bob123", "Bob", "Student", User.Role.STUDENT);
        User created = userService.createUser(newUser);

        assertNotNull(created, "Created user should not be null");
        assertTrue(created.getId() > 0, "Created user should have a valid ID");
        assertEquals("bob@test.com", created.getEmail());
        assertEquals("Bob", created.getFirstName());
        System.out.println("✓ Created new user Bob (ID: " + created.getId() + ")");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Update user information")
    void testUpdateUser() {
        Optional<User> bob = userService.getUserByEmail("bob@test.com");
        assertTrue(bob.isPresent(), "Bob should exist");

        User user = bob.get();
        user.setFirstName("Robert");
        user.setRole(User.Role.TEACHER);

        boolean updated = userService.updateUser(user);
        assertTrue(updated, "User update should succeed");

        // Verify changes persisted
        Optional<User> updatedUser = userService.getUserById(user.getId());
        assertTrue(updatedUser.isPresent(), "Updated user should be found");
        assertEquals("Robert", updatedUser.get().getFirstName());
        assertEquals(User.Role.TEACHER, updatedUser.get().getRole());
        System.out.println("✓ Updated Bob to Robert with TEACHER role");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Search users by name")
    void testSearchUsers() {
        List<User> results = userService.searchUsers("robert");

        assertNotNull(results, "Search results should not be null");
        assertTrue(results.size() >= 1, "Should find at least Robert");
        assertTrue(results.stream().anyMatch(u -> u.getFirstName().equalsIgnoreCase("Robert")),
                "Should find Robert in results");
        System.out.println("✓ Search for 'robert' found " + results.size() + " result(s)");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Search users by email")
    void testSearchUsersByEmail() {
        List<User> results = userService.searchUsers("teacher@");

        assertNotNull(results, "Search results should not be null");
        assertTrue(results.stream().anyMatch(u -> u.getEmail().contains("teacher@")),
                "Should find teacher email in results");
        System.out.println("✓ Search for 'teacher@' found " + results.size() + " result(s)");
    }

    @Test
    @Order(11)
    @DisplayName("Test 11: Count users by role")
    void testCountUsersByRole() {
        int adminCount = userService.countUsersByRole(User.Role.ADMIN);
        int teacherCount = userService.countUsersByRole(User.Role.TEACHER);
        int studentCount = userService.countUsersByRole(User.Role.STUDENT);

        assertTrue(adminCount >= 1, "Should have at least 1 admin");
        assertTrue(teacherCount >= 2, "Should have at least 2 teachers (John + Robert)");
        assertTrue(studentCount >= 1, "Should have at least 1 student (Jane)");

        System.out.println("✓ User counts - Admins: " + adminCount + ", Teachers: " + teacherCount + ", Students: " + studentCount);
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Get total user count")
    void testGetTotalUserCount() {
        int totalCount = userService.getTotalUserCount();

        assertTrue(totalCount >= 4, "Should have at least 4 users total");
        System.out.println("✓ Total user count: " + totalCount);
    }

    @Test
    @Order(13)
    @DisplayName("Test 13: Delete user")
    void testDeleteUser() {
        Optional<User> robert = userService.getUserByEmail("bob@test.com");
        assertTrue(robert.isPresent(), "Robert should exist");
        int robertId = robert.get().getId();

        boolean deleted = userService.deleteUser(robertId);
        assertTrue(deleted, "User deletion should succeed");

        // Verify user is gone
        Optional<User> deletedUser = userService.getUserById(robertId);
        assertFalse(deletedUser.isPresent(), "Deleted user should not be found");
        System.out.println("✓ Deleted user Robert (ID: " + robertId + ")");
    }

    @Test
    @Order(14)
    @DisplayName("Test 14: Cannot delete non-existent user")
    void testDeleteNonExistentUser() {
        boolean deleted = userService.deleteUser(99999);
        assertFalse(deleted, "Deleting non-existent user should fail");
        System.out.println("✓ Cannot delete non-existent user");
    }
}
