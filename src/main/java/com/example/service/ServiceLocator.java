package com.example.service;

/**
 * Simple service locator pattern for dependency injection.
 *
 * Provides access to database-backed service implementations.
 *
 * Usage: ServiceLocator.getAuthService().login(email, password);
 */
public class ServiceLocator {

    private static AuthService authService;
    private static UserService userService;
    private static QuizService quizService;
    private static AttemptService attemptService;

    static {
        initialize();
    }

    private static void initialize() {
        // Initialize database services
        authService = new DatabaseAuthService();
        userService = new DatabaseUserService();
        quizService = new DatabaseQuizService(userService);
        attemptService = new DatabaseAttemptService();
    }
    
    public static AuthService getAuthService() {
        return authService;
    }
    
    public static UserService getUserService() {
        return userService;
    }
    
    public static QuizService getQuizService() {
        return quizService;
    }
    
    public static AttemptService getAttemptService() {
        return attemptService;
    }
    
    /**
     * Reinitialize all services (useful for testing).
     */
    public static void reset() {
        initialize();
    }
}
