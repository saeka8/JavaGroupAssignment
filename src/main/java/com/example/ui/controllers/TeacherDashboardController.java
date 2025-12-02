package com.example.ui.controllers;

import com.example.model.Group;
import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.QuizAttempt;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the Teacher Dashboard.
 * Allows teachers to create quizzes, assign them, and view results.
 */
public class TeacherDashboardController {

    // Header
    @FXML private Label welcomeLabel;
    @FXML private Label userEmailLabel;

    // Stats
    @FXML private Label myQuizzesCount;
    @FXML private Label totalAttemptsCount;
    @FXML private Label averageScoreLabel;

    // Groups Table
    @FXML private TableView<Group> groupsTable;
    @FXML private TableColumn<Group, String> groupNameColumn;
    @FXML private TableColumn<Group, Integer> groupStudentsColumn;
    @FXML private TableColumn<Group, Integer> groupQuizzesColumn;
    @FXML private Label selectedGroupLabel;

    // My Quizzes Table
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, String> quizDescriptionColumn;
    @FXML private TableColumn<Quiz, String> quizQuestionsColumn;
    @FXML private TableColumn<Quiz, String> quizAttemptsColumn;
    @FXML private TableColumn<Quiz, String> quizAvgScoreColumn;
    @FXML private TextField quizSearchField;

    // Results Table
    @FXML private TableView<QuizAttempt> resultsTable;
    @FXML private TableColumn<QuizAttempt, String> resultStudentColumn;
    @FXML private TableColumn<QuizAttempt, String> resultQuizColumn;
    @FXML private TableColumn<QuizAttempt, String> resultScoreColumn;
    @FXML private TableColumn<QuizAttempt, String> resultDateColumn;

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

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupHeader();
        setupGroupsTable();
        setupQuizzesTable();
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
                selectedGroupLabel.setText("📚 Selected: " + newSelection.getName() + " - Manage quizzes for this group below");
                loadQuizzesForSelectedGroup();
                updateStatsForGroup();
            } else {
                selectedGroup = null;
                selectedGroupLabel.setText("💡 Select a group to manage its quizzes");
                quizzesTable.setItems(FXCollections.observableArrayList());
                myQuizzesCount.setText("0");
                totalAttemptsCount.setText("0");
                averageScoreLabel.setText("N/A");
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
            int quizId = cellData.getValue().getId();
            double avg = attemptService.getAverageScoreForQuiz(quizId);
            if (avg == 0 && attemptService.getAttemptsByQuiz(quizId).isEmpty()) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(String.format("%.1f%%", avg));
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
                    if (!item.equals("N/A")) {
                        int score = Integer.parseInt(item.replace("%", ""));
                        if (score >= 80) {
                            setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                        } else if (score >= 60) {
                            setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                        }
                    } else {
                        setStyle("-fx-text-fill: #6c757d;");
                    }
                }
            }
        });
    }

    private void setupResultsTable() {
        resultStudentColumn.setCellValueFactory(cellData -> {
            int studentId = cellData.getValue().getStudentId();
            String name = userService.getUserById(studentId)
                    .map(User::getFullName)
                    .orElse("Unknown Student");
            return new SimpleStringProperty(name);
        });

        resultQuizColumn.setCellValueFactory(cellData -> {
            int quizId = cellData.getValue().getQuizId();
            String title = quizService.getQuizById(quizId)
                    .map(Quiz::getTitle)
                    .orElse("Unknown Quiz");
            return new SimpleStringProperty(title);
        });

        resultScoreColumn.setCellValueFactory(cellData -> {
            int score = cellData.getValue().getTotalScore();
            return new SimpleStringProperty(String.format("%.1f%%", score));
        });

        resultScoreColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int score = Integer.parseInt(item.replace("%", ""));
                    if (score >= 80) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else if (score >= 60) {
                        setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    }
                }
            }
        });

        resultDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
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
        if (currentUser == null) return;

        // Load teacher's groups
        List<Group> groups = groupService.getGroupsByTeacher(currentUser.getId());
        groupsTable.setItems(FXCollections.observableArrayList(groups));

        // Don't load quizzes until a group is selected
        // User must select a group first
    }

    private void loadQuizzesForSelectedGroup() {
        if (selectedGroup == null) return;

        // Load quizzes for the selected group
        List<Quiz> quizzes = quizService.getQuizzesByGroup(selectedGroup.getId());
        myQuizzes = FXCollections.observableArrayList(quizzes);
        filteredQuizzes = new FilteredList<>(myQuizzes, p -> true);
        quizzesTable.setItems(filteredQuizzes);

        // Load all attempts for this group's quizzes
        List<QuizAttempt> allAttempts = quizzes.stream()
                .flatMap(q -> attemptService.getAttemptsByQuiz(q.getId()).stream())
                .sorted((a, b) -> b.getAttemptedAt().compareTo(a.getAttemptedAt()))
                .collect(Collectors.toList());
        resultsTable.setItems(FXCollections.observableArrayList(allAttempts));
    }

    private void updateStatsForGroup() {
        if (selectedGroup == null) return;

        List<Quiz> quizzes = quizService.getQuizzesByGroup(selectedGroup.getId());
        List<QuizAttempt> attempts = quizzes.stream()
                .flatMap(q -> attemptService.getAttemptsByQuiz(q.getId()).stream())
                .collect(Collectors.toList());

        myQuizzesCount.setText(String.valueOf(quizzes.size()));
        totalAttemptsCount.setText(String.valueOf(attempts.size()));

        if (attempts.isEmpty()) {
            averageScoreLabel.setText("N/A");
        } else {
            double avg = attempts.stream()
                    .mapToDouble(QuizAttempt::getScore)
                    .average()
                    .orElse(0.0);
            averageScoreLabel.setText(String.format("%.1f%%", avg));
        }
    }

    private void updateStats(List<Quiz> quizzes, List<QuizAttempt> attempts) {
        myQuizzesCount.setText(String.valueOf(quizzes.size()));
        totalAttemptsCount.setText(String.valueOf(attempts.size()));

        if (attempts.isEmpty()) {
            averageScoreLabel.setText("N/A");
        } else {
            double avg = attempts.stream()
                    .mapToDouble(QuizAttempt::getScore)
                    .average()
                    .orElse(0.0);
            averageScoreLabel.setText(String.format("%.1f%%", avg));
        }
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
                    quiz.setGroupId(selectedGroup.getId());  // Set the group ID
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
                    "Use the '❓ Add Questions' button to add questions to this quiz.");
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
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        if (selectedGroup != null) {
            loadQuizzesForSelectedGroup();
            updateStatsForGroup();
        }
        quizSearchField.clear();
    }

    @FXML
    private void handleAddQuestions() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected",
                    "Please select a quiz from the table to add questions.");
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

        final int[] questionsAdded = {0};

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
                            score
                    );

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
}
