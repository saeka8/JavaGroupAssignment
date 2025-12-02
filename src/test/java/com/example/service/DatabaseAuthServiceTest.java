package com.example.service;

import com.example.model.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DatabaseAuthService - verifies authentication and registration persistence.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseAuthServiceTest {

    private static DatabaseAuthService authService;
    private static final String TEST_DB_PATH = "test_group5Quiz.db";

    @BeforeAll
    static void setupDatabase() {
        // Delete existing test database
        File testDb = new File(TEST_DB_PATH);
        if (testDb.exists()) {
            testDb.delete();
        }

        // Initialize auth service (will create database)
        authService = new DatabaseAuthService();
        System.out.println("✓ Test database created: " + TEST_DB_PATH);
    }

    @AfterAll
    static void cleanupDatabase() {
        // Keep the database for inspection, but you could delete it here
        System.out.println("✓ Tests completed. Test database preserved for inspection.");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Register new admin user")
    void testRegisterAdmin() {
        User admin = new User("admin@test.com", "admin123", "Admin", "User", User.Role.ADMIN);
        boolean result = authService.register(admin);

        assertTrue(result, "Admin registration should succeed");
        System.out.println("✓ Admin user registered successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Register new teacher user")
    void testRegisterTeacher() {
        User teacher = new User("teacher@test.com", "teacher123", "John", "Teacher", User.Role.TEACHER);
        boolean result = authService.register(teacher);

        assertTrue(result, "Teacher registration should succeed");
        System.out.println("✓ Teacher user registered successfully");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Register new student user")
    void testRegisterStudent() {
        User student = new User("student@test.com", "student123", "Jane", "Student", User.Role.STUDENT);
        boolean result = authService.register(student);

        assertTrue(result, "Student registration should succeed");
        System.out.println("✓ Student user registered successfully");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Cannot register duplicate email")
    void testRegisterDuplicateEmail() {
        User duplicate = new User("admin@test.com", "password", "Another", "Admin", User.Role.ADMIN);
        boolean result = authService.register(duplicate);

        assertFalse(result, "Duplicate email registration should fail");
        System.out.println("✓ Duplicate email correctly rejected");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Login with correct admin credentials")
    void testLoginAdmin() {
        Optional<User> result = authService.login("admin@test.com", "admin123");

        assertTrue(result.isPresent(), "Login should succeed");
        User user = result.get();
        assertEquals("admin@test.com", user.getEmail());
        assertEquals("Admin", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals(User.Role.ADMIN, user.getRole());
        System.out.println("✓ Admin login successful with correct credentials");
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Login with correct teacher credentials")
    void testLoginTeacher() {
        Optional<User> result = authService.login("teacher@test.com", "teacher123");

        assertTrue(result.isPresent(), "Login should succeed");
        User user = result.get();
        assertEquals("teacher@test.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Teacher", user.getLastName());
        assertEquals(User.Role.TEACHER, user.getRole());
        System.out.println("✓ Teacher login successful with correct credentials");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Login with correct student credentials")
    void testLoginStudent() {
        Optional<User> result = authService.login("student@test.com", "student123");

        assertTrue(result.isPresent(), "Login should succeed");
        User user = result.get();
        assertEquals("student@test.com", user.getEmail());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Student", user.getLastName());
        assertEquals(User.Role.STUDENT, user.getRole());
        System.out.println("✓ Student login successful with correct credentials");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Login fails with wrong password")
    void testLoginWrongPassword() {
        Optional<User> result = authService.login("admin@test.com", "wrongpassword");

        assertFalse(result.isPresent(), "Login should fail with wrong password");
        System.out.println("✓ Wrong password correctly rejected");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Login fails with non-existent email")
    void testLoginNonExistentUser() {
        Optional<User> result = authService.login("nonexistent@test.com", "password");

        assertFalse(result.isPresent(), "Login should fail with non-existent email");
        System.out.println("✓ Non-existent user correctly rejected");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Check if email exists")
    void testEmailExists() {
        assertTrue(authService.emailExists("admin@test.com"), "Admin email should exist");
        assertTrue(authService.emailExists("teacher@test.com"), "Teacher email should exist");
        assertTrue(authService.emailExists("student@test.com"), "Student email should exist");
        assertFalse(authService.emailExists("nobody@test.com"), "Non-existent email should not exist");
        System.out.println("✓ Email existence checks working correctly");
    }

    @Test
    @Order(11)
    @DisplayName("Test 11: Validate email format")
    void testEmailValidation() {
        assertTrue(authService.isValidEmail("valid@example.com"), "Valid email should pass");
        assertTrue(authService.isValidEmail("user.name@domain.co.uk"), "Valid email with dots should pass");
        assertFalse(authService.isValidEmail("invalid"), "Invalid email should fail");
        assertFalse(authService.isValidEmail("@example.com"), "Email without local part should fail");
        assertFalse(authService.isValidEmail("user@"), "Email without domain should fail");
        System.out.println("✓ Email validation working correctly");
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Validate password requirements")
    void testPasswordValidation() {
        assertTrue(authService.isValidPassword("12345"), "5-character password should pass");
        assertTrue(authService.isValidPassword("verylongpassword123"), "Long password should pass");
        assertFalse(authService.isValidPassword("1234"), "4-character password should fail");
        assertFalse(authService.isValidPassword(""), "Empty password should fail");
        System.out.println("✓ Password validation working correctly");
    }
}
