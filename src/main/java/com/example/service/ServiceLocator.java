package com.example.service;

import com.example.service.mock.MockAttemptService;
import com.example.service.mock.MockAuthService;
import com.example.service.mock.MockQuizService;
import com.example.service.mock.MockUserService;

/**
 * Simple service locator pattern for dependency injection.
 * 
 * Currently uses mock implementations for UI testing.
 * When the real services are ready, just change the initialization here.
 * 
 * Usage: ServiceLocator.getAuthService().login(email, password);
 */
public class ServiceLocator {
    
    private static AuthService authService;
    private static UserService userService;
    private static QuizService quizService;
    private static AttemptService attemptService;
    
    // Flag to indicate whether to use mocks or real implementations
    private static boolean useMocks = true;
    
    static {
        initialize();
    }
    
    private static void initialize() {
        if (useMocks) {
            // Initialize mock services
            authService = new MockAuthService();
            MockUserService mockUserService = new MockUserService();
            userService = mockUserService;
            quizService = new MockQuizService(mockUserService);
            attemptService = new MockAttemptService();
        } else {
            // TODO: Initialize real services when ready
            // authService = new RealAuthService();
            // userService = new RealUserService();
            // etc.
        }
    }
    
    /**
     * Switch between mock and real implementations.
     * Call this before any services are accessed.
     */
    public static void setUseMocks(boolean useMocks) {
        ServiceLocator.useMocks = useMocks;
        initialize();
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
