package com.example.ui.controllers;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.model.Group;
import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.QuizAttempt;
import com.example.quizlogic.Question;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for the Teacher Dashboard.
 * Allows teachers to create quizzes, assign them, and view results.
 */
public class TeacherDashboardController {

    // Header
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label userEmailLabel;

    // Stats
    @FXML
    private Label myQuizzesCount;
    @FXML
    private Label totalAttemptsCount;
    @FXML
    private Label averageScoreLabel;

    // Groups Table
    @FXML
    private TableView<Group> groupsTable;
    @FXML
    private TableColumn<Group, String> groupNameColumn;
    @FXML
    private TableColumn<Group, Integer> groupStudentsColumn;
    @FXML
    private TableColumn<Group, Integer> groupQuizzesColumn;
    @FXML
    private Label selectedGroupLabel;

    // My Quizzes Table
    @FXML
    private TableView<Quiz> quizzesTable;
    @FXML
    private TableColumn<Quiz, String> quizTitleColumn;
    @FXML
    private TableColumn<Quiz, String> quizDescriptionColumn;
    @FXML
    private TableColumn<Quiz, String> quizQuestionsColumn;
    @FXML
    private TableColumn<Quiz, String> quizAttemptsColumn;
    @FXML
    private TableColumn<Quiz, String> quizAvgScoreColumn;
    @FXML
    private TextField quizSearchField;

    // Students Table (new tab)
    @FXML
    private TableView<User> studentsTable;
    @FXML
    private TableColumn<User, String> studentNameColumn;
    @FXML
    private TableColumn<User, String> studentEmailColumn;
    @FXML
    private TableColumn<User, String> studentGroupAvgColumn;

    // Results Table (modified)
    @FXML
    private TableView<QuizAttempt> resultsTable;
    @FXML
    private TableColumn<QuizAttempt, Integer> resultAttemptColumn;
    @FXML
    private TableColumn<QuizAttempt, String> resultScoreColumn;
    @FXML
    private TableColumn<QuizAttempt, String> resultPercentageColumn;
    @FXML
    private TableColumn<QuizAttempt, String> resultDateColumn;
    @FXML
    private Label resultsFilterLabel;

    // Services
    private final QuizService quizService = ServiceLocator.getQuizService();
    private final AttemptService attemptService = ServiceLocator.getAttemptService();
    private final UserService userService = ServiceLocator.getUserService();
    private final GroupService groupService = ServiceLocator.getGroupService();

    // Data
    private ObservableList<Quiz> myQuizzes;
    private FilteredList<Quiz> filteredQuizzes;
    private User currentUser;
    private Group selectedGroup;
    private Quiz selectedQuiz;
    private User selectedStudent;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupHeader();
        setupGroupsTable();
        setupQuizzesTable();
        setupStudentsTable();
        setupResultsTable();
        setupSearch();
        loadData();
    }

    private void setupHeader() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + "!");
            userEmailLabel.setText(currentUser.getEmail());
        }
    }

    private void setupGroupsTable() {
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        groupStudentsColumn.setCellValueFactory(cellData -> {
            int count = groupService.getStudentsInGroup(cellData.getValue().getId()).size();
            return new SimpleIntegerProperty(count).asObject();
        });

        groupQuizzesColumn.setCellValueFactory(cellData -> {
            int groupId = cellData.getValue().getId();
            int count = quizService.getQuizzesByGroup(groupId).size();
            return new SimpleIntegerProperty(count).asObject();
        });

        // Listen for group selection changes
        groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGroup = newSelection;
                selectedGroupLabel
                        .setText("📚 Selected: " + newSelection.getName() + " - Manage quizzes for this group below");
                loadQuizzesForSelectedGroup();
                loadStudentsForSelectedGroup();
                updateStatsForGroup();
                clearResultsFilter();
            } else {
                selectedGroup = null;
                selectedGroupLabel.setText("💡 Select a group to manage its quizzes");
                quizzesTable.setItems(FXCollections.observableArrayList());
                studentsTable.setItems(FXCollections.observableArrayList());
                myQuizzesCount.setText("0");
                totalAttemptsCount.setText("0");
                averageScoreLabel.setText("N/A");
                clearResultsFilter();
            }
        });

        // If the underlying groups list changes (e.g. group deleted elsewhere), clear selection if it no longer exists
        groupsTable.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Group> ch) -> {
            if (selectedGroup != null && !groupsTable.getItems().contains(selectedGroup)) {
                groupsTable.getSelectionModel().clearSelection();
                selectedGroup = null;
                quizzesTable.setItems(FXCollections.observableArrayList());
                studentsTable.setItems(FXCollections.observableArrayList());
                clearResultsFilter();
            }
        });
    }

    private void setupQuizzesTable() {
        quizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quizDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        quizQuestionsColumn.setCellValueFactory(cellData -> {
            int count = cellData.getValue().getQuestionCount();
            return new SimpleStringProperty(String.valueOf(count));
        });

        quizAttemptsColumn.setCellValueFactory(cellData -> {
            int quizId = cellData.getValue().getId();
            int attempts = attemptService.getAttemptsByQuiz(quizId).size();
            return new SimpleStringProperty(String.valueOf(attempts));
        });

        quizAvgScoreColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            List<QuizAttempt> attempts = attemptService.getAttemptsByQuiz(quiz.getId());

            if (attempts.isEmpty()) {
                return new SimpleStringProperty("N/A");
            }

            // Calculate total possible score
            int totalScore = quiz.getQuestions().stream()
                    .mapToInt(Question::getAssignedScore)
                    .sum();

            if (totalScore == 0) {
                return new SimpleStringProperty("N/A");
            }

            // Calculate average score
            double avgScore = attempts.stream()
                    .mapToInt(QuizAttempt::getTotalScore)
                    .average()
                    .orElse(0.0);

            // Calculate percentage
            double percentage = (avgScore / totalScore) * 100;

            return new SimpleStringProperty(String.format("%.1f%%", percentage));
        });

        // Color code average scores
        quizAvgScoreColumn.setCellFactory(column -> new TableCell<>() {
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

        // Listen for quiz selection
        quizzesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedQuiz = newSelection;
            updateResultsFilter();
        });

        // If the underlying quizzes list changes (e.g. quizzes removed by group deletion), clear selection if it no longer exists
        quizzesTable.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Quiz> ch) -> {
            if (selectedQuiz != null && !quizzesTable.getItems().contains(selectedQuiz)) {
                quizzesTable.getSelectionModel().clearSelection();
                selectedQuiz = null;
                clearResultsFilter();
            }
        });
    }

    private void setupStudentsTable() {
        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        studentGroupAvgColumn.setCellValueFactory(cellData -> {
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
                            .mapToInt(Question::getAssignedScore)
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
        studentGroupAvgColumn.setCellFactory(column -> new TableCell<>() {
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

        // Listen for student selection
        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedStudent = newSelection;
            updateResultsFilter();
        });
    }

    private void setupResultsTable() {
        resultAttemptColumn.setCellValueFactory(new PropertyValueFactory<>("attemptNumber"));

        resultScoreColumn.setCellValueFactory(cellData -> {
            QuizAttempt attempt = cellData.getValue();
            int score = attempt.getTotalScore();

            // Get quiz to calculate total
            Quiz quiz = quizService.getQuizById(attempt.getQuizId()).orElse(null);
            if (quiz == null) {
                return new SimpleStringProperty(score + " pts");
            }

            int totalScore = quiz.getQuestions().stream()
                    .mapToInt(Question::getAssignedScore)
                    .sum();

            return new SimpleStringProperty(String.format("%d/%d pts", score, totalScore));
        });

        resultPercentageColumn.setCellValueFactory(cellData -> {
            QuizAttempt attempt = cellData.getValue();
            int score = attempt.getTotalScore();

            Quiz quiz = quizService.getQuizById(attempt.getQuizId()).orElse(null);
            if (quiz == null) {
                return new SimpleStringProperty("N/A");
            }

            int totalScore = quiz.getQuestions().stream()
                    .mapToInt(Question::getAssignedScore)
                    .sum();

            if (totalScore == 0) {
                return new SimpleStringProperty("N/A");
            }

            double percentage = (score * 100.0) / totalScore;
            return new SimpleStringProperty(String.format("%.1f%%", percentage));
        });

        // Color code percentage
        resultPercentageColumn.setCellFactory(column -> new TableCell<>() {
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

        resultDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            String date = cellData.getValue().getAttemptedAt().format(formatter);
            return new SimpleStringProperty(date);
        });
    }

    private void setupSearch() {
        quizSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredQuizzes.setPredicate(quiz -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerFilter = newVal.toLowerCase();
                return quiz.getTitle().toLowerCase().contains(lowerFilter) ||
                        quiz.getDescription().toLowerCase().contains(lowerFilter);
            });
        });
    }

    private void loadData() {
        if (currentUser == null)
            return;

        // Load teacher's groups
        List<Group> groups = groupService.getGroupsByTeacher(currentUser.getId());
        groupsTable.setItems(FXCollections.observableArrayList(groups));

        // Don't load quizzes until a group is selected
        // User must select a group first
    }

    private void loadQuizzesForSelectedGroup() {
        if (selectedGroup == null)
            return;

        // Load quizzes for the selected group
        List<Quiz> quizzes = quizService.getQuizzesByGroup(selectedGroup.getId());
        myQuizzes = FXCollections.observableArrayList(quizzes);
        filteredQuizzes = new FilteredList<>(myQuizzes, p -> true);
        quizzesTable.setItems(filteredQuizzes);
    }

    private void loadStudentsForSelectedGroup() {
        if (selectedGroup == null) {
            studentsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<User> students = groupService.getStudentsInGroup(selectedGroup.getId());
        studentsTable.setItems(FXCollections.observableArrayList(students));
    }

    private void updateStatsForGroup() {
        if (selectedGroup == null)
            return;

        List<Quiz> quizzes = quizService.getQuizzesByGroup(selectedGroup.getId());
        List<QuizAttempt> attempts = quizzes.stream()
                .flatMap(q -> attemptService.getAttemptsByQuiz(q.getId()).stream())
                .collect(Collectors.toList());

        myQuizzesCount.setText(String.valueOf(quizzes.size()));
        totalAttemptsCount.setText(String.valueOf(attempts.size()));

        if (attempts.isEmpty() || quizzes.isEmpty()) {
            averageScoreLabel.setText("N/A");
        } else {
            // Calculate average percentage across all attempts
            List<Double> percentages = new ArrayList<>();

            for (QuizAttempt attempt : attempts) {
                Quiz quiz = quizService.getQuizById(attempt.getQuizId()).orElse(null);
                if (quiz != null) {
                    int totalScore = quiz.getQuestions().stream()
                            .mapToInt(Question::getAssignedScore)
                            .sum();
                    if (totalScore > 0) {
                        double percentage = (attempt.getTotalScore() * 100.0) / totalScore;
                        percentages.add(percentage);
                    }
                }
            }

            if (percentages.isEmpty()) {
                averageScoreLabel.setText("N/A");
            } else {
                double avgPercentage = percentages.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
                averageScoreLabel.setText(String.format("%.1f%%", avgPercentage));
            }
        }
    }

    private void updateResultsFilter() {
        // Use current selections from the tables to avoid stale references
        Quiz currentSelectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        User currentSelectedStudent = studentsTable.getSelectionModel().getSelectedItem();

        if (currentSelectedQuiz == null || currentSelectedStudent == null) {
            clearResultsFilter();
            return;
        }

        // Load attempts for the specific student and quiz
        List<QuizAttempt> attempts = attemptService.getStudentAttemptsForQuiz(currentSelectedStudent.getId(), currentSelectedQuiz.getId());

        // Sort by attempt number (most recent first)
        attempts.sort((a, b) -> Integer.compare(b.getAttemptNumber(), a.getAttemptNumber()));

        resultsTable.setItems(FXCollections.observableArrayList(attempts));

        resultsFilterLabel.setText(String.format("📊 Showing attempts for: %s - %s",
            currentSelectedStudent.getFullName(), currentSelectedQuiz.getTitle()));
    }

    private void clearResultsFilter() {
        resultsTable.setItems(FXCollections.observableArrayList());
        resultsFilterLabel.setText("💡 Select a quiz from 'My Quizzes' tab and a student from 'Student List' tab to view their attempts");
    }

    @FXML
    private void handleCreateQuiz() {
        // Check if a group is selected
        if (selectedGroup == null) {
            showAlert(Alert.AlertType.WARNING, "No Group Selected",
                    "Please select a group first before creating a quiz.");
            return;
        }

        // Show a dialog to create a new quiz
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Create New Quiz");
        dialog.setHeaderText("Enter quiz details for group: " + selectedGroup.getName());

        ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Quiz Title");
        titleField.setPrefWidth(300);

        TextArea descField = new TextArea();
        descField.setPromptText("Description (optional)");
        descField.setPrefRowCount(3);
        descField.setPrefWidth(300);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Group:"), 0, 2);
        grid.add(new Label(selectedGroup.getName()), 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                String title = titleField.getText().trim();
                if (!title.isEmpty()) {
                    Quiz quiz = new Quiz();
                    quiz.setTitle(title);
                    quiz.setDescription(descField.getText().trim());
                    quiz.setTeacherId(currentUser.getId());
                    quiz.setGroupId(selectedGroup.getId());
                    quiz.setTeacherName(currentUser.getFullName());
                    return quizService.createQuiz(quiz);
                }
            }
            return null;
        });

        Optional<Quiz> result = dialog.showAndWait();
        result.ifPresent(quiz -> {
            showAlert(Alert.AlertType.INFORMATION, "Quiz Created",
                    "Quiz '" + quiz.getTitle() + "' created successfully!\n\n" +
                            "Use the '❓ Manage Questions' button to add questions to this quiz.");
            // Refresh groups table to update quiz counts
            groupsTable.refresh();
            loadQuizzesForSelectedGroup();
            updateStatsForGroup();
        });
    }

    @FXML
    private void handleAssignQuiz() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected",
                    "Please select a quiz from the table to assign.");
            return;
        }

        if (!isCurrentUserAuthor(selectedQuiz)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied",
                    "You are not the author of this quiz and cannot assign it.");
            return;
        }

        // Get all students
        List<User> students = userService.getUsersByRole(User.Role.STUDENT);
        if (students.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Students",
                    "There are no students registered in the system.");
            return;
        }

        // Show assignment dialog with checkboxes
        Dialog<List<User>> dialog = new Dialog<>();
        dialog.setTitle("Assign Quiz");
        dialog.setHeaderText("Select students to assign: " + selectedQuiz.getTitle());

        ButtonType assignButton = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButton, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        List<CheckBox> checkBoxes = students.stream()
                .map(student -> {
                    CheckBox cb = new CheckBox(student.getFullName() + " (" + student.getEmail() + ")");
                    cb.setUserData(student);
                    // Pre-check if already assigned
                    if (quizService.isQuizAssigned(selectedQuiz.getId(), student.getId())) {
                        cb.setSelected(true);
                        cb.setStyle("-fx-text-fill: #28a745;");
                    }
                    return cb;
                })
                .collect(Collectors.toList());

        // Select All / Deselect All buttons
        HBox buttonBox = new HBox(10);
        Button selectAll = new Button("Select All");
        selectAll.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(true)));
        Button deselectAll = new Button("Deselect All");
        deselectAll.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));
        buttonBox.getChildren().addAll(selectAll, deselectAll);

        ScrollPane scrollPane = new ScrollPane();
        VBox checkBoxContainer = new VBox(8);
        checkBoxContainer.getChildren().addAll(checkBoxes);
        scrollPane.setContent(checkBoxContainer);
        scrollPane.setPrefHeight(200);
        scrollPane.setFitToWidth(true);

        content.getChildren().addAll(buttonBox, new Separator(), scrollPane);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(400);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButton) {
                return checkBoxes.stream()
                        .filter(CheckBox::isSelected)
                        .map(cb -> (User) cb.getUserData())
                        .collect(Collectors.toList());
            }
            return null;
        });

        Optional<List<User>> result = dialog.showAndWait();
        result.ifPresent(selectedStudents -> {
            List<Integer> studentIds = selectedStudents.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            if (!studentIds.isEmpty()) {
                quizService.assignQuizToStudents(selectedQuiz.getId(), studentIds);
                showAlert(Alert.AlertType.INFORMATION, "Quiz Assigned",
                        "Quiz assigned to " + studentIds.size() + " student(s).");
            }
        });
    }

    @FXML
    private void handleDeleteQuiz() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected",
                    "Please select a quiz from the table to delete.");
            return;
        }
        if (!isCurrentUserAuthor(selectedQuiz)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied",
                    "You are not the author of this quiz and cannot delete it.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Quiz");
        confirm.setHeaderText("Are you sure you want to delete this quiz?");
        confirm.setContentText("Quiz: " + selectedQuiz.getTitle() +
                "\n\nThis action cannot be undone. All associated data will be lost.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            quizService.deleteQuiz(selectedQuiz.getId());
            showAlert(Alert.AlertType.INFORMATION, "Quiz Deleted",
                    "Quiz '" + selectedQuiz.getTitle() + "' has been deleted.");
            loadData();
            if (selectedGroup != null) {
                loadQuizzesForSelectedGroup();
                updateStatsForGroup();
            }
        }
    }

    @FXML
    private void handleEditQuiz() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected",
                    "Please select a quiz from the table to edit.");
            return;
        }

        if (!isCurrentUserAuthor(selectedQuiz)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied",
                    "You are not the author of this quiz and cannot edit it.");
            return;
        }

        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Edit Quiz");
        dialog.setHeaderText("Edit details for: " + selectedQuiz.getTitle());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(selectedQuiz.getTitle());
        titleField.setPromptText("Quiz Title");
        titleField.setPrefWidth(300);

        TextArea descField = new TextArea(selectedQuiz.getDescription());
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        descField.setPrefWidth(300);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String title = titleField.getText().trim();
                if (!title.isEmpty()) {
                    selectedQuiz.setTitle(title);
                    selectedQuiz.setDescription(descField.getText().trim());
                    return selectedQuiz;
                }
            }
            return null;
        });

        Optional<Quiz> result = dialog.showAndWait();
        result.ifPresent(quiz -> {
            if (quizService.updateQuiz(quiz)) {
                showAlert(Alert.AlertType.INFORMATION, "Quiz Updated",
                        "Quiz details updated successfully.");
                quizzesTable.refresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update quiz.");
            }
        });
    }

    @FXML
    private void handleManageQuestions() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected",
                    "Please select a quiz from the table to manage questions.");
            return;
        }

        if (!isCurrentUserAuthor(selectedQuiz)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied",
                    "You are not the author of this quiz and cannot manage its questions.");
            return;
        }

        // Create a custom dialog for managing questions
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Questions");
        dialog.setHeaderText("Manage questions for: " + selectedQuiz.getTitle());

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        // Layout
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefSize(600, 400);

        // Table for questions
        TableView<com.example.quizlogic.Question> questionsTable = new TableView<>();

        TableColumn<com.example.quizlogic.Question, String> questionCol = new TableColumn<>("Question");
        questionCol.setCellValueFactory(new PropertyValueFactory<>("text"));
        questionCol.setPrefWidth(300);

        TableColumn<com.example.quizlogic.Question, Integer> scoreCol = new TableColumn<>("Points");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("assignedScore"));
        scoreCol.setPrefWidth(50);

        questionsTable.getColumns().addAll(questionCol, scoreCol);

        // Load questions
        List<com.example.quizlogic.Question> questions = quizService.getQuestionsByQuiz(selectedQuiz.getId());
        ObservableList<com.example.quizlogic.Question> questionsList = FXCollections.observableArrayList(questions);
        questionsTable.setItems(questionsList);

        // Buttons
        HBox actions = new HBox(10);
        Button addBtn = new Button("Add New");
        Button editBtn = new Button("Edit Selected");
        Button deleteBtn = new Button("Delete Selected");

        addBtn.setOnAction(e -> {
            handleAddQuestionInternal(selectedQuiz, questionsList);
        });

        editBtn.setOnAction(e -> {
            com.example.quizlogic.Question selectedQ = questionsTable.getSelectionModel().getSelectedItem();
            if (selectedQ != null) {
                handleEditQuestionInternal(selectedQ, questionsList);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Select a question to edit.");
            }
        });

        deleteBtn.setOnAction(e -> {
            com.example.quizlogic.Question selectedQ = questionsTable.getSelectionModel().getSelectedItem();
            if (selectedQ != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Question");
                confirm.setHeaderText("Delete this question?");
                confirm.setContentText(selectedQ.getText());

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    if (quizService.deleteQuestion(selectedQ.getId())) {
                        questionsList.remove(selectedQ);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete question.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Select a question to delete.");
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        content.getChildren().addAll(questionsTable, actions);

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();

        // Refresh main table stats
        loadQuizzesForSelectedGroup();
    }

    private void handleAddQuestionInternal(Quiz quiz, ObservableList<com.example.quizlogic.Question> list) {
        Dialog<com.example.quizlogic.Question> dialog = createQuestionDialog(null);
        dialog.setTitle("Add Question");

        dialog.showAndWait().ifPresent(q -> {
            if (quizService.addQuestionToQuiz(quiz.getId(), q.getText(), q.getOptionA(), q.getOptionB(),
                    q.getOptionC(), q.getOptionD(), q.getCorrectAnswer(), q.getAssignedScore())) {
                // Reload to get the ID
                list.setAll(quizService.getQuestionsByQuiz(quiz.getId()));
            }
        });
    }

    private void handleEditQuestionInternal(com.example.quizlogic.Question q,
            ObservableList<com.example.quizlogic.Question> list) {
        Dialog<com.example.quizlogic.Question> dialog = createQuestionDialog(q);
        dialog.setTitle("Edit Question");

        dialog.showAndWait().ifPresent(updatedQ -> {
            // Update the existing object with new values
            q.setText(updatedQ.getText());
            q.setOptionA(updatedQ.getOptionA());
            q.setOptionB(updatedQ.getOptionB());
            q.setOptionC(updatedQ.getOptionC());
            q.setOptionD(updatedQ.getOptionD());
            q.setCorrectAnswer(updatedQ.getCorrectAnswer());
            q.setAssignedScore(updatedQ.getAssignedScore());

            if (quizService.updateQuestion(q)) {
                // Refresh list view
                int index = list.indexOf(q);
                list.set(index, q);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update question.");
            }
        });
    }

    private Dialog<com.example.quizlogic.Question> createQuestionDialog(com.example.quizlogic.Question existing) {
        Dialog<com.example.quizlogic.Question> dialog = new Dialog<>();
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField(existing != null ? existing.getText() : "");
        TextField optionAField = new TextField(existing != null ? existing.getOptionA() : "");
        TextField optionBField = new TextField(existing != null ? existing.getOptionB() : "");
        TextField optionCField = new TextField(existing != null ? existing.getOptionC() : "");
        TextField optionDField = new TextField(existing != null ? existing.getOptionD() : "");

        ComboBox<String> correctOptionCombo = new ComboBox<>();
        correctOptionCombo.getItems().addAll("A", "B", "C", "D");
        correctOptionCombo.setValue(existing != null ? String.valueOf(existing.getCorrectAnswer()) : "A");

        TextField scoreField = new TextField(existing != null ? String.valueOf(existing.getAssignedScore()) : "1");

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Option A:"), 0, 1);
        grid.add(optionAField, 1, 1);
        grid.add(new Label("Option B:"), 0, 2);
        grid.add(optionBField, 1, 2);
        grid.add(new Label("Option C:"), 0, 3);
        grid.add(optionCField, 1, 3);
        grid.add(new Label("Option D:"), 0, 4);
        grid.add(optionDField, 1, 4);
        grid.add(new Label("Correct:"), 0, 5);
        grid.add(correctOptionCombo, 1, 5);
        grid.add(new Label("Points:"), 0, 6);
        grid.add(scoreField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    int score = Integer.parseInt(scoreField.getText().trim());
                    return new com.example.quizlogic.Question(
                            0, // ID ignored for new, preserved for edit in caller
                            questionField.getText().trim(),
                            optionAField.getText().trim(),
                            optionBField.getText().trim(),
                            optionCField.getText().trim(),
                            optionDField.getText().trim(),
                            correctOptionCombo.getValue().charAt(0),
                            score);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    @FXML
    private void handleRefresh() {
        loadData();
        if (selectedGroup != null) {
            loadQuizzesForSelectedGroup();
            loadStudentsForSelectedGroup();
            updateStatsForGroup();
        }
        quizSearchField.clear();
        clearResultsFilter();
    }

    @FXML
    private void handleAddQuestions() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected",
                    "Please select a quiz from the table to add questions.");
            return;
        }

        if (!isCurrentUserAuthor(selectedQuiz)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied",
                    "You are not the author of this quiz and cannot add questions.");
            return;
        }

        // Show dialog for adding multiple choice questions
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Add Question to Quiz");
        dialog.setHeaderText("Add question to: " + selectedQuiz.getTitle());

        ButtonType addButton = new ButtonType("Add Question", ButtonBar.ButtonData.OK_DONE);
        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, doneButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField();
        questionField.setPromptText("Question text");
        questionField.setPrefWidth(400);

        TextField optionAField = new TextField();
        optionAField.setPromptText("Option A");

        TextField optionBField = new TextField();
        optionBField.setPromptText("Option B");

        TextField optionCField = new TextField();
        optionCField.setPromptText("Option C");

        TextField optionDField = new TextField();
        optionDField.setPromptText("Option D");

        ComboBox<String> correctOptionCombo = new ComboBox<>();
        correctOptionCombo.getItems().addAll("A", "B", "C", "D");
        correctOptionCombo.setValue("A");

        TextField scoreField = new TextField("1");
        scoreField.setPromptText("Points");
        scoreField.setPrefWidth(80);

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Option A:"), 0, 1);
        grid.add(optionAField, 1, 1);
        grid.add(new Label("Option B:"), 0, 2);
        grid.add(optionBField, 1, 2);
        grid.add(new Label("Option C:"), 0, 3);
        grid.add(optionCField, 1, 3);
        grid.add(new Label("Option D:"), 0, 4);
        grid.add(optionDField, 1, 4);
        grid.add(new Label("Correct Option:"), 0, 5);
        grid.add(correctOptionCombo, 1, 5);
        grid.add(new Label("Points:"), 0, 6);
        grid.add(scoreField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        final int[] questionsAdded = { 0 };

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String question = questionField.getText().trim();
                String optionA = optionAField.getText().trim();
                String optionB = optionBField.getText().trim();
                String optionC = optionCField.getText().trim();
                String optionD = optionDField.getText().trim();
                String correctOption = correctOptionCombo.getValue();
                String scoreText = scoreField.getText().trim();

                if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                        optionC.isEmpty() || optionD.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error",
                            "All fields are required.");
                    return false;
                }

                try {
                    int score = Integer.parseInt(scoreText);
                    if (score <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error",
                                "Points must be a positive number.");
                        return false;
                    }

                    // Add question to quiz
                    boolean success = quizService.addQuestionToQuiz(
                            selectedQuiz.getId(),
                            question,
                            optionA, optionB, optionC, optionD,
                            correctOption.charAt(0),
                            score);

                    if (success) {
                        questionsAdded[0]++;
                        // Clear fields for next question
                        questionField.clear();
                        optionAField.clear();
                        optionBField.clear();
                        optionCField.clear();
                        optionDField.clear();
                        correctOptionCombo.setValue("A");
                        scoreField.setText("1");
                        questionField.requestFocus();
                        return false; // Keep dialog open
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Failed to add question.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error",
                            "Points must be a valid number.");
                    return false;
                }
            }
            return true; // Close dialog on "Done"
        });

        dialog.showAndWait();

        if (questionsAdded[0] > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Questions Added",
                    questionsAdded[0] + " question(s) added to quiz successfully!");
            loadQuizzesForSelectedGroup();
        }
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

    // Helper: check if current user is the author of a quiz or is admin
    private boolean isCurrentUserAuthor(Quiz quiz) {
        if (currentUser == null || quiz == null)
            return false;
        if (currentUser.getRole() == User.Role.ADMIN)
            return true;
        return quiz.getTeacherId() == currentUser.getId();
    }
}
