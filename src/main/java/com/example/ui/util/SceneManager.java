package com.example.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized manager for scene navigation.
 * Handles loading FXML files and switching scenes.
 */
public class SceneManager {
    
    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<String, String> viewPaths;
    private Object currentController;
    
    // View constants
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String ADMIN_DASHBOARD = "admin_dashboard";
    public static final String TEACHER_DASHBOARD = "teacher_dashboard";
    public static final String STUDENT_DASHBOARD = "student_dashboard";
    public static final String QUIZ_TAKE = "quiz_take";
    public static final String QUIZ_CREATE = "quiz_create";
    public static final String QUIZ_RESULTS = "quiz_results";
    public static final String USER_MANAGEMENT = "user_management";
    
    private SceneManager() {
        viewPaths = new HashMap<>();
        initializeViewPaths();
    }
    
    /**
     * Get the singleton instance.
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    /**
     * Initialize the stage reference. Call this from Main.java
     */
    public void initialize(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Map view names to their FXML file paths.
     */
    private void initializeViewPaths() {
        viewPaths.put(LOGIN, "/views/login.fxml");
        viewPaths.put(REGISTER, "/views/register.fxml");
        viewPaths.put(ADMIN_DASHBOARD, "/views/admin_dashboard.fxml");
        viewPaths.put(TEACHER_DASHBOARD, "/views/teacher_dashboard.fxml");
        viewPaths.put(STUDENT_DASHBOARD, "/views/student_dashboard.fxml");
        viewPaths.put(QUIZ_TAKE, "/views/quiz_take.fxml");
        viewPaths.put(QUIZ_CREATE, "/views/quiz_create.fxml");
        viewPaths.put(QUIZ_RESULTS, "/views/quiz_results.fxml");
        viewPaths.put(USER_MANAGEMENT, "/views/user_management.fxml");
    }
    
    /**
     * Switch to a different scene by view name.
     * @param viewName one of the view constants
     */
    public void switchScene(String viewName) {
        switchScene(viewName, 900, 600);
    }
    
    /**
     * Switch to a different scene with custom dimensions.
     */
    public void switchScene(String viewName, int width, int height) {
        String path = viewPaths.get(viewName);
        if (path == null) {
            System.err.println("Unknown view: " + viewName);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            currentController = loader.getController();
            
            Scene scene = new Scene(root, width, height);
            
            // Apply the global stylesheet
            String css = getClass().getResource("/styles/main.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(getTitleForView(viewName));
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Failed to load view: " + viewName);
            e.printStackTrace();
        }
    }
    
    /**
     * Load a view and return its controller (useful for passing data).
     */
    public <T> T loadViewWithController(String viewName) {
        String path = viewPaths.get(viewName);
        if (path == null) {
            System.err.println("Unknown view: " + viewName);
            return null;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            T controller = loader.getController();
            currentController = controller;
            
            Scene scene = new Scene(root, 900, 600);
            String css = getClass().getResource("/styles/main.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(getTitleForView(viewName));
            primaryStage.show();
            
            return controller;
            
        } catch (IOException e) {
            System.err.println("Failed to load view: " + viewName);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get the current controller.
     */
    public Object getCurrentController() {
        return currentController;
    }
    
    /**
     * Get the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Get window title for each view.
     */
    private String getTitleForView(String viewName) {
        return switch (viewName) {
            case LOGIN -> "Quiz Platform - Login";
            case REGISTER -> "Quiz Platform - Register";
            case ADMIN_DASHBOARD -> "Quiz Platform - Admin Dashboard";
            case TEACHER_DASHBOARD -> "Quiz Platform - Teacher Dashboard";
            case STUDENT_DASHBOARD -> "Quiz Platform - Student Dashboard";
            case QUIZ_TAKE -> "Quiz Platform - Take Quiz";
            case QUIZ_CREATE -> "Quiz Platform - Create Quiz";
            case QUIZ_RESULTS -> "Quiz Platform - Quiz Results";
            case USER_MANAGEMENT -> "Quiz Platform - User Management";
            default -> "Quiz Platform";
        };
    }
}
