package com.example.ui.controllers;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.service.AttemptService;
import com.example.service.QuizService;
import com.example.service.ServiceLocator;
import com.example.service.UserService;
import com.example.ui.util.SceneManager;
import com.example.ui.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the Admin Dashboard.
 * Allows admins to manage users and view platform statistics.
 */
public class AdminDashboardController {

    // Header
    @FXML private Label welcomeLabel;
    @FXML private Label userEmailLabel;

    // Stats
    @FXML private Label totalUsersCount;
    @FXML private Label totalQuizzesCount;
    @FXML private Label totalAttemptsCount;
    @FXML private Label teacherCount;
    @FXML private Label studentCount;

    // Users Table
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private TextField userSearchField;
    @FXML private ComboBox<String> roleFilterCombo;

    // Quizzes Table
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, String> quizTeacherColumn;
    @FXML private TableColumn<Quiz, String> quizQuestionsColumn;

    // Services
    private final UserService userService = ServiceLocator.getUserService();
    private final QuizService quizService = ServiceLocator.getQuizService();
    private final AttemptService attemptService = ServiceLocator.getAttemptService();

    // Data
    private ObservableList<User> allUsers;
    private FilteredList<User> filteredUsers;
    private User currentUser;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupHeader();
        setupRoleFilter();
        setupUsersTable();
        setupQuizzesTable();
        setupSearch();
        loadData();
    }

    private void setupHeader() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + "!");
            userEmailLabel.setText(currentUser.getEmail());
        }
    }

    private void setupRoleFilter() {
        roleFilterCombo.getItems().addAll("All Roles", "Admin", "Teacher", "Student");
        roleFilterCombo.setValue("All Roles");
        roleFilterCombo.setOnAction(e -> applyFilters());
    }

    private void setupUsersTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getFullName()));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getRole().toString()));

        // Style role column with colors
        userRoleColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ADMIN" -> setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                        case "TEACHER" -> setStyle("-fx-text-fill: #5C80BC; -fx-font-weight: bold;");
                        case "STUDENT" -> setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });
    }

    private void setupQuizzesTable() {
        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        quizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        quizTeacherColumn.setCellValueFactory(cellData -> {
            int teacherId = cellData.getValue().getTeacherId();
            String name = userService.getUserById(teacherId)
                    .map(User::getFullName)
                    .orElse("Unknown");
            return new SimpleStringProperty(name);
        });

        quizQuestionsColumn.setCellValueFactory(cellData -> {
            int count = cellData.getValue().getQuestionCount();
            return new SimpleStringProperty(String.valueOf(count));
        });
    }

    private void setupSearch() {
        userSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredUsers.setPredicate(user -> {
            // Role filter
            String selectedRole = roleFilterCombo.getValue();
            if (selectedRole != null && !selectedRole.equals("All Roles")) {
                if (!user.getRole().toString().equalsIgnoreCase(selectedRole)) {
                    return false;
                }
            }

            // Search filter
            String searchText = userSearchField.getText();
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerFilter = searchText.toLowerCase();
            return user.getFullName().toLowerCase().contains(lowerFilter) ||
                   user.getEmail().toLowerCase().contains(lowerFilter);
        });
    }

    private void loadData() {
        // Load users
        List<User> users = userService.getAllUsers();
        allUsers = FXCollections.observableArrayList(users);
        filteredUsers = new FilteredList<>(allUsers, p -> true);
        usersTable.setItems(filteredUsers);

        // Load quizzes
        List<Quiz> quizzes = quizService.getAllQuizzes();
        quizzesTable.setItems(FXCollections.observableArrayList(quizzes));

        // Update stats
        updateStats();
    }

    private void updateStats() {
        totalUsersCount.setText(String.valueOf(userService.getTotalUserCount()));
        totalQuizzesCount.setText(String.valueOf(quizService.getTotalQuizCount()));
        totalAttemptsCount.setText(String.valueOf(attemptService.getTotalAttemptCount()));
        teacherCount.setText(String.valueOf(userService.countUsersByRole(User.Role.TEACHER)));
        studentCount.setText(String.valueOf(userService.countUsersByRole(User.Role.STUDENT)));
    }

    @FXML
    private void handleAddUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Student", "Teacher", "Admin");
        roleCombo.setValue("Student");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText();
                String roleStr = roleCombo.getValue();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
                    return null;
                }

                if (userService.getUserByEmail(email).isPresent()) {
                    showAlert(Alert.AlertType.ERROR, "Email Exists", "A user with this email already exists.");
                    return null;
                }

                User.Role role = switch (roleStr) {
                    case "Admin" -> User.Role.ADMIN;
                    case "Teacher" -> User.Role.TEACHER;
                    default -> User.Role.STUDENT;
                };

                User newUser = new User(email, password, firstName, lastName, role);
                return userService.createUser(newUser);
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            showAlert(Alert.AlertType.INFORMATION, "User Created",
                    "User '" + user.getFullName() + "' created successfully!");
            loadData();
        });
    }

    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No User Selected",
                    "Please select a user from the table to edit.");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + selectedUser.getFullName());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField(selectedUser.getFirstName());
        TextField lastNameField = new TextField(selectedUser.getLastName());
        TextField emailField = new TextField(selectedUser.getEmail());
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Student", "Teacher", "Admin");
        roleCombo.setValue(selectedUser.getRole().toString().substring(0, 1) + 
                          selectedUser.getRole().toString().substring(1).toLowerCase());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String roleStr = roleCombo.getValue();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
                    return null;
                }

                // Check if email changed and already exists
                if (!email.equalsIgnoreCase(selectedUser.getEmail())) {
                    if (userService.getUserByEmail(email).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Email Exists", "A user with this email already exists.");
                        return null;
                    }
                }

                User.Role role = switch (roleStr) {
                    case "Admin" -> User.Role.ADMIN;
                    case "Teacher" -> User.Role.TEACHER;
                    default -> User.Role.STUDENT;
                };

                selectedUser.setFirstName(firstName);
                selectedUser.setLastName(lastName);
                selectedUser.setEmail(email);
                selectedUser.setRole(role);

                if (userService.updateUser(selectedUser)) {
                    return selectedUser;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            showAlert(Alert.AlertType.INFORMATION, "User Updated",
                    "User '" + user.getFullName() + "' updated successfully!");
            loadData();
        });
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No User Selected",
                    "Please select a user from the table to delete.");
            return;
        }

        // Prevent deleting yourself
        if (selectedUser.getId() == currentUser.getId()) {
            showAlert(Alert.AlertType.ERROR, "Cannot Delete",
                    "You cannot delete your own account.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Are you sure you want to delete this user?");
        confirm.setContentText("User: " + selectedUser.getFullName() + " (" + selectedUser.getEmail() + ")\n\n" +
                "This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userService.deleteUser(selectedUser.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "User Deleted",
                        "User '" + selectedUser.getFullName() + "' has been deleted.");
                loadData();
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        userSearchField.clear();
        roleFilterCombo.setValue("All Roles");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchScene(SceneManager.LOGIN, 400, 500);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
