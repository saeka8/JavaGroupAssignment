package com.example;

import com.example.model.User;
import com.example.service.AuthService;
import com.example.service.ServiceLocator;
import com.example.ui.util.SceneManager;
import com.example.ui.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

/**
 * Controller for the Login screen.
 * Handles user authentication and routes to appropriate dashboard based on role.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = ServiceLocator.getAuthService();

    @FXML
    private void initialize() {
        // Clear error on typing
        emailField.textProperty().addListener((obs, old, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearError());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        // Attempt login
        Optional<User> userOpt = authService.login(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Set the current session
            SessionManager.getInstance().setCurrentUser(user);
            
            // Route to appropriate dashboard based on role
            SceneManager sceneManager = SceneManager.getInstance();
            switch (user.getRole()) {
                case ADMIN -> sceneManager.switchScene(SceneManager.ADMIN_DASHBOARD);
                case TEACHER -> sceneManager.switchScene(SceneManager.TEACHER_DASHBOARD);
                case STUDENT -> sceneManager.switchScene(SceneManager.STUDENT_DASHBOARD);
            }
        } else {
            showError("Invalid email or password.");
            passwordField.clear();
        }
    }

    @FXML
    private void handleRegister() {
        SceneManager.getInstance().switchScene(SceneManager.REGISTER, 400, 600);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setVisible(false);
    }
}
