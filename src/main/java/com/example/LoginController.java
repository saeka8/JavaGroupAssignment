package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Just printing on console. We need logic here
        if ("user".equals(username) && "pass".equals(password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed!");
        }
    }
}
