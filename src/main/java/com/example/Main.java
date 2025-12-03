package com.example;

import com.example.database.DatabaseManager;
import com.example.ui.dev.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main entry point for the Quiz Platform application.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize the SceneManager with the primary stage
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initialize(primaryStage);
        
        // Start with the login screen
        sceneManager.switchScene(SceneManager.LOGIN, 400, 500);
    }

    public static void main(String[] args) throws SQLException {
        // Connect with database
        Connection conn = DatabaseManager.connectWithDatabase();
        // Create all the tables if necessary
        DatabaseManager.createAllTables(conn);

        launch(args);
    }
}
