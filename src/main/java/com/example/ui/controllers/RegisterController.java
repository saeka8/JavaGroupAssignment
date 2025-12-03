package com.example.ui.controllers;

import com.example.model.User;
import com.example.service.AuthService;
import com.example.service.ServiceLocator;
import com.example.ui.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Registration screen.
 */
public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    // Role selection removed: new registrations will always create STUDENT accounts.
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private final AuthService authService = ServiceLocator.getAuthService();

    @FXML
    private void initialize() {
        // No role selection in registration UI.

        // Clear messages on typing
        firstNameField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        lastNameField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        emailField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> clearMessages());
    }

    @FXML
    private void handleRegister() {
        // Get values
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        // Registrations always create STUDENT role

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!authService.isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }

        if (!authService.isValidPassword(password)) {
            showError("Password must be at least 5 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (authService.emailExists(email)) {
            showError("An account with this email already exists.");
            return;
        }

        // Create user (registrations always create STUDENT role)
        User.Role role = User.Role.STUDENT;
        User newUser = User.createUser(email, password, firstName, lastName, role);

        if (authService.register(newUser)) {
            showSuccess("Account created successfully! You can now sign in.");
            clearForm();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        SceneManager.getInstance().switchScene(SceneManager.LOGIN, 400, 500);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void clearMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
