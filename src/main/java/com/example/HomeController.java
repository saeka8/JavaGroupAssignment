package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    // Buttons
    @FXML private Button loginButton;
    @FXML private Button demoButton;

    // Stats labels
    @FXML private Label totalUsersLabel;
    @FXML private Label activeQuizzesLabel;
    @FXML private Label quizzesTodayLabel;

    // Info / status message
    @FXML private Label infoLabel;

    @FXML
    private void initialize() {
        if (totalUsersLabel != null) totalUsersLabel.setText("—");
        if (activeQuizzesLabel != null) activeQuizzesLabel.setText("—");
        if (quizzesTodayLabel != null) quizzesTodayLabel.setText("—");
        if (infoLabel != null) infoLabel.setText("");
    }

    @FXML
    private void onLoginButtonClicked() {
        System.out.println("[Home] Go to Login clicked.");
        if (infoLabel != null) {
            infoLabel.setText("This will navigate to the login screen once it’s wired.");
        }
    }

    @FXML
    private void onHomeClicked() {
        if (infoLabel != null) {
            infoLabel.setText("You are already on the home page.");
        }
    }

    @FXML
    private void onAboutClicked() {
        if (infoLabel != null) {
            infoLabel.setText("This app manages quizzes for students, teachers, and admins.");
        }
    }

    @FXML
    private void onHelpClicked() {
        if (infoLabel != null) {
            infoLabel.setText("Help page not implemented yet. Your team can link docs here.");
        }
    }
}
