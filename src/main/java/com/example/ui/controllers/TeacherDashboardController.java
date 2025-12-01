package com.example.ui.controllers;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.QuizAttempt;
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

    // Data
    private ObservableList<Quiz> myQuizzes;
    private FilteredList<Quiz> filteredQuizzes;
    private User currentUser;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupHeader();
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
                        double score = Double.parseDouble(item.replace("%", ""));
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
            double score = cellData.getValue().getScore();
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
                    double score = Double.parseDouble(item.replace("%", ""));
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

        // Load teacher's quizzes
        List<Quiz> quizzes = quizService.getQuizzesByTeacher(currentUser.getId());
        myQuizzes = FXCollections.observableArrayList(quizzes);
        filteredQuizzes = new FilteredList<>(myQuizzes, p -> true);
        quizzesTable.setItems(filteredQuizzes);

        // Load all attempts for teacher's quizzes
        List<QuizAttempt> allAttempts = quizzes.stream()
                .flatMap(q -> attemptService.getAttemptsByQuiz(q.getId()).stream())
                .sorted((a, b) -> b.getAttemptedAt().compareTo(a.getAttemptedAt()))
                .collect(Collectors.toList());
        resultsTable.setItems(FXCollections.observableArrayList(allAttempts));

        // Update stats
        updateStats(quizzes, allAttempts);
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
        // Show a dialog to create a new quiz
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Create New Quiz");
        dialog.setHeaderText("Enter quiz details");

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

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                String title = titleField.getText().trim();
                if (!title.isEmpty()) {
                    Quiz quiz = new Quiz();
                    quiz.setTitle(title);
                    quiz.setDescription(descField.getText().trim());
                    quiz.setTeacherId(currentUser.getId());
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
                    "Note: You can add questions to this quiz in a future update.");
            loadData();
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
        quizSearchField.clear();
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
