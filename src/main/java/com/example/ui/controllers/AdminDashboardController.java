package com.example.ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.model.Group;
import com.example.model.Quiz;
import com.example.model.User;
import com.example.service.AttemptService;
import com.example.service.GroupService;
import com.example.service.QuizService;
import com.example.service.ServiceLocator;
import com.example.service.UserService;
import com.example.ui.util.SceneManager;
import com.example.ui.util.SessionManager;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    // Groups Table
    @FXML private TableView<Group> groupsTable;
    @FXML private TableColumn<Group, Integer> groupIdColumn;
    @FXML private TableColumn<Group, String> groupNameColumn;
    @FXML private TableColumn<Group, String> groupTeacherColumn;
    @FXML private TableColumn<Group, Integer> groupStudentCountColumn;

    // Enrollment UI
    @FXML private TableView<User> enrolledStudentsTable;
    @FXML private TableColumn<User, Integer> enrolledStudentIdColumn;
    @FXML private TableColumn<User, String> enrolledStudentNameColumn;
    @FXML private TableColumn<User, String> enrolledStudentEmailColumn;
    @FXML private TableColumn<User, String> enrolledStudentAvgColumn;

    // Services
    private final UserService userService = ServiceLocator.getUserService();
    private final QuizService quizService = ServiceLocator.getQuizService();
    private final AttemptService attemptService = ServiceLocator.getAttemptService();
    private final GroupService groupService = ServiceLocator.getGroupService();

    // Data
    private ObservableList<User> allUsers;
    private FilteredList<User> filteredUsers;
    private User currentUser;
    private ObservableList<Group> allGroups;
    private Group selectedGroup; // Track selected group for enrollment management

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupHeader();
        setupRoleFilter();
        setupUsersTable();
        setupQuizzesTable();
        setupGroupsTable();
        setupEnrollmentUI();
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

        // Load groups so the Group Management tab is populated on initial load
        List<Group> groups = groupService.getAllGroups();
        allGroups = FXCollections.observableArrayList(groups);
        groupsTable.setItems(allGroups);

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
            // If deleting a teacher who owns groups, require reassignment first
            if (selectedUser.getRole() == User.Role.TEACHER) {
                List<Group> teacherGroups = groupService.getGroupsByTeacher(selectedUser.getId());
                if (!teacherGroups.isEmpty()) {
                    // Prepare list of other teachers
                    List<User> otherTeachers = userService.getUsersByRole(User.Role.TEACHER).stream()
                            .filter(t -> t.getId() != selectedUser.getId())
                            .collect(Collectors.toList());

                    if (otherTeachers.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Cannot Delete Teacher",
                                "This teacher owns groups and there are no other teachers to reassign them to.\nPlease create or assign another teacher first.");
                        return;
                    }

                    // Dialog to choose a new teacher per group
                    Dialog<Boolean> mappingDialog = new Dialog<>();
                    mappingDialog.setTitle("Reassign Groups");
                    mappingDialog.setHeaderText("Select a teacher for each group owned by: " + selectedUser.getFullName());
                    ButtonType confirmBtn = new ButtonType("Reassign & Delete", ButtonBar.ButtonData.OK_DONE);
                    mappingDialog.getDialogPane().getButtonTypes().addAll(confirmBtn, ButtonType.CANCEL);

                    VBox content = new VBox(10);
                    content.setPadding(new Insets(10));

                    // Map groupId -> ComboBox<User>
                    java.util.Map<Integer, ComboBox<User>> mapping = new java.util.HashMap<>();

                    for (Group g : teacherGroups) {
                        HBox row = new HBox(10);
                        Label gLabel = new Label(g.getName());
                        gLabel.setPrefWidth(220);
                        ComboBox<User> cb = new ComboBox<>(FXCollections.observableArrayList(otherTeachers));
                        cb.setCellFactory(lv -> new ListCell<>() {
                            @Override
                            protected void updateItem(User item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? null : item.getFullName());
                            }
                        });
                        cb.setButtonCell(new ListCell<>() {
                            @Override
                            protected void updateItem(User item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? null : item.getFullName());
                            }
                        });
                        row.getChildren().addAll(gLabel, cb);
                        content.getChildren().add(row);
                        mapping.put(g.getId(), cb);
                    }

                    ScrollPane sp = new ScrollPane(content);
                    sp.setPrefSize(520, Math.min(400, 80 + teacherGroups.size() * 40));
                    mappingDialog.getDialogPane().setContent(sp);

                    mappingDialog.setResultConverter(btn -> {
                        if (btn == confirmBtn) return Boolean.TRUE;
                        return null;
                    });

                    Optional<Boolean> mapRes = mappingDialog.showAndWait();
                    if (mapRes.isPresent() && mapRes.get()) {
                        // Validate all selections
                        for (Group g : teacherGroups) {
                            ComboBox<User> cb = mapping.get(g.getId());
                            if (cb.getValue() == null) {
                                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a teacher for group: " + g.getName());
                                return;
                            }
                        }

                        boolean allOk = true;
                        for (Group g : teacherGroups) {
                            ComboBox<User> cb = mapping.get(g.getId());
                            User newTeacher = cb.getValue();
                            // Update the group's teacher id
                            g.setTeacherId(newTeacher.getId());
                            if (!groupService.updateGroup(g)) {
                                allOk = false;
                            }
                        }

                        if (!allOk) {
                            showAlert(Alert.AlertType.ERROR, "Reassignment Failed", "One or more groups failed to be reassigned. Aborting deletion.");
                            return;
                        }

                        // After reassignments, delete the teacher
                        if (userService.deleteUser(selectedUser.getId())) {
                            showAlert(Alert.AlertType.INFORMATION, "User Deleted",
                                    "User '" + selectedUser.getFullName() + "' has been deleted and their groups reassigned.");
                            loadData();
                        }
                        return;
                    } else {
                        // Cancelled
                        return;
                    }
                }
            }

            // Default path: no reassignment needed
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

    private void setupGroupsTable() {
        groupIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        groupTeacherColumn.setCellValueFactory(cellData -> {
            int teacherId = cellData.getValue().getTeacherId();
            String name = userService.getUserById(teacherId)
                    .map(User::getFullName)
                    .orElse("Unknown");
            return new SimpleStringProperty(name);
        });

        groupStudentCountColumn.setCellValueFactory(cellData -> {
            int count = groupService.getStudentsInGroup(cellData.getValue().getId()).size();
            return new SimpleIntegerProperty(count).asObject();
        });

        // Listen for selection changes to load enrolled students
        groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGroup = newSelection;
                loadEnrolledStudents(newSelection.getId());
            } else {
                selectedGroup = null;
                enrolledStudentsTable.setItems(FXCollections.observableArrayList());
            }
        });
    }

    private void setupEnrollmentUI() {
        enrolledStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        enrolledStudentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));
        enrolledStudentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // NEW: Setup average grade column
        enrolledStudentAvgColumn.setCellValueFactory(cellData -> {
            if (selectedGroup == null) {
                return new SimpleStringProperty("N/A");
            }

            User student = cellData.getValue();
            List<Quiz> groupQuizzes = quizService.getQuizzesByGroup(selectedGroup.getId());

            if (groupQuizzes.isEmpty()) {
                return new SimpleStringProperty("N/A");
            }

            // Calculate average percentage across all quizzes in the group (best attempts only)
            List<Double> percentages = new ArrayList<>();

            for (Quiz quiz : groupQuizzes) {
                Optional<Integer> bestScore = attemptService.getBestScore(student.getId(), quiz.getId());
                if (bestScore.isPresent()) {
                    int totalScore = quiz.getQuestions().stream()
                            .mapToInt(com.example.quizlogic.Question::getAssignedScore)
                            .sum();
                    if (totalScore > 0) {
                        double percentage = (bestScore.get() * 100.0) / totalScore;
                        percentages.add(percentage);
                    }
                }
            }

            if (percentages.isEmpty()) {
                return new SimpleStringProperty("N/A");
            }

            double avgPercentage = percentages.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            return new SimpleStringProperty(String.format("%.1f%%", avgPercentage));
        });

        // Color code the average column
        enrolledStudentAvgColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("N/A")) {
                        setStyle("-fx-text-fill: #6c757d;");
                    } else {
                        try {
                            double percentage = Double.parseDouble(item.replace("%", ""));
                            if (percentage >= 80) {
                                setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            } else if (percentage >= 60) {
                                setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                            } else {
                                setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            }
                        } catch (Exception e) {
                            setStyle("-fx-text-fill: #6c757d;");
                        }
                    }
                }
            }
        });
    }

    private void loadEnrolledStudents(int groupId) {
        List<User> students = groupService.getStudentsInGroup(groupId);
        enrolledStudentsTable.setItems(FXCollections.observableArrayList(students));
    }

    @FXML
    private void handleCreateGroup() {
        Dialog<Group> dialog = new Dialog<>();
        dialog.setTitle("Create New Group");
        dialog.setHeaderText("Enter group details and select students");

        ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);

        VBox mainVBox = new VBox(15);
        mainVBox.setPadding(new Insets(20, 20, 10, 20));

        // Group Name
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label nameLabel = new Label("Group Name:");
        nameLabel.setPrefWidth(100);
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name");
        groupNameField.setPrefWidth(300);
        nameBox.getChildren().addAll(nameLabel, groupNameField);

        // Teacher Selection
        HBox teacherBox = new HBox(10);
        teacherBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label teacherLabel = new Label("Teacher:");
        teacherLabel.setPrefWidth(100);
        ComboBox<User> teacherCombo = new ComboBox<>();
        List<User> teachers = userService.getUsersByRole(User.Role.TEACHER);
        teacherCombo.setItems(FXCollections.observableArrayList(teachers));
        teacherCombo.setPrefWidth(300);
        teacherCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
        teacherCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
        teacherBox.getChildren().addAll(teacherLabel, teacherCombo);

        // Student Selection with Checkboxes
        Label studentsLabel = new Label("Select Students:");
        studentsLabel.setStyle("-fx-font-weight: bold;");

        ScrollPane studentScrollPane = new ScrollPane();
        studentScrollPane.setPrefHeight(200);
        studentScrollPane.setFitToWidth(true);

        VBox studentCheckboxes = new VBox(5);
        studentCheckboxes.setPadding(new Insets(10));

        List<User> allStudents = userService.getUsersByRole(User.Role.STUDENT);
        List<CheckBox> checkBoxes = new ArrayList<>();

        for (User student : allStudents) {
            CheckBox cb = new CheckBox(student.getFullName() + " (" + student.getEmail() + ")");
            cb.setUserData(student);
            checkBoxes.add(cb);
            studentCheckboxes.getChildren().add(cb);
        }

        studentScrollPane.setContent(studentCheckboxes);

        mainVBox.getChildren().addAll(nameBox, teacherBox, studentsLabel, studentScrollPane);
        dialog.getDialogPane().setContent(mainVBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                String groupName = groupNameField.getText().trim();
                User selectedTeacher = teacherCombo.getValue();

                if (groupName.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Group name is required.");
                    return null;
                }

                if (selectedTeacher == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a teacher.");
                    return null;
                }

                if (groupService.getGroupByName(groupName).isPresent()) {
                    showAlert(Alert.AlertType.ERROR, "Group Exists", "A group with this name already exists.");
                    return null;
                }

                // Create the group
                Group newGroup = new Group(groupName, selectedTeacher.getId());
                Group createdGroup = groupService.createGroup(newGroup);

                // Enroll selected students
                if (createdGroup != null) {
                    List<User> selectedStudents = checkBoxes.stream()
                            .filter(CheckBox::isSelected)
                            .map(cb -> (User) cb.getUserData())
                            .collect(Collectors.toList());

                    for (User student : selectedStudents) {
                        groupService.enrollStudent(createdGroup.getId(), student.getId());
                    }
                }

                return createdGroup;
            }
            return null;
        });

        Optional<Group> result = dialog.showAndWait();
        result.ifPresent(group -> {
            showAlert(Alert.AlertType.INFORMATION, "Group Created",
                    "Group '" + group.getName() + "' created successfully with students enrolled!");
            handleRefreshGroups();
        });
    }

    @FXML
    private void handleDeleteGroup() {
        Group selectedGroup = groupsTable.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showAlert(Alert.AlertType.WARNING, "No Group Selected",
                    "Please select a group from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Group");
        confirm.setHeaderText("Are you sure you want to delete this group?");
        confirm.setContentText("Group: " + selectedGroup.getName() + "\n\n" +
                "This will remove all enrollments. This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (groupService.deleteGroup(selectedGroup.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Group Deleted",
                        "Group '" + selectedGroup.getName() + "' has been deleted.");
                handleRefreshGroups();
            }
        }
    }

    @FXML
    private void handleRefreshGroups() {
        List<Group> groups = groupService.getAllGroups();
        allGroups = FXCollections.observableArrayList(groups);
        groupsTable.setItems(allGroups);
    }

    @FXML
    private void handleEnrollStudent() {
        // UPDATED: Use selected group from groups table instead of combo box
        if (selectedGroup == null) {
            showAlert(Alert.AlertType.WARNING, "No Group Selected",
                    "Please select a group from the 'Group Management' section above.");
            return;
        }

        // Show a dialog to select student to enroll
        List<User> allStudents = userService.getUsersByRole(User.Role.STUDENT);
        List<User> enrolledStudents = groupService.getStudentsInGroup(selectedGroup.getId());

        // Filter out already enrolled students
        List<User> availableStudents = allStudents.stream()
                .filter(s -> enrolledStudents.stream().noneMatch(e -> e.getId() == s.getId()))
                .collect(java.util.stream.Collectors.toList());

        if (availableStudents.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Students Available",
                    "All students are already enrolled in this group.");
            return;
        }

        // Create selection dialog
        javafx.scene.control.ChoiceDialog<User> dialog = new javafx.scene.control.ChoiceDialog<>(
                availableStudents.get(0), availableStudents);
        dialog.setTitle("Enroll Student");
        dialog.setHeaderText("Enroll student in: " + selectedGroup.getName());
        dialog.setContentText("Select student:");

        // Format the combo box items
        dialog.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK).setDisable(false);

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(student -> {
            if (groupService.enrollStudent(selectedGroup.getId(), student.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Student Enrolled",
                        "Student '" + student.getFullName() + "' enrolled in '" + selectedGroup.getName() + "'.");
                loadEnrolledStudents(selectedGroup.getId());
                handleRefreshGroups(); // Refresh to update student counts
            } else {
                showAlert(Alert.AlertType.ERROR, "Enrollment Failed",
                        "Failed to enroll student in this group.");
            }
        });
    }

    @FXML
    private void handleRemoveStudent() {
        User selectedStudent = enrolledStudentsTable.getSelectionModel().getSelectedItem();
        Group selectedGroup = groupsTable.getSelectionModel().getSelectedItem();

        if (selectedGroup == null) {
            showAlert(Alert.AlertType.WARNING, "No Group Selected",
                    "Please select a group from the groups table above.");
            return;
        }

        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "No Student Selected",
                    "Please select a student from the enrolled students table.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Student");
        confirm.setHeaderText("Remove student from group?");
        confirm.setContentText("Student: " + selectedStudent.getFullName() + "\n" +
                "Group: " + selectedGroup.getName());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (groupService.removeStudent(selectedGroup.getId(), selectedStudent.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Student Removed",
                        "Student removed from group successfully.");
                loadEnrolledStudents(selectedGroup.getId());
                handleRefreshGroups(); // Refresh to update student counts
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
