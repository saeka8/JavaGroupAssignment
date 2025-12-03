package com.example.ui.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.database.DatabaseManager;
import com.example.database.RetrieveFromDatabase;
import com.example.model.Group;
import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.QuizAttempt;
import com.example.quizlogic.Question;
import com.example.service.AttemptService;
import com.example.service.QuizService;
import com.example.service.ServiceLocator;
import com.example.ui.util.SceneManager;
import com.example.ui.util.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * Controller for the Student Dashboard.
 * Shows assigned quizzes and attempt history.
 */
public class StudentDashboardController {

    // Header
    @FXML private Label welcomeLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label groupsLabel;

    // Stats
    @FXML private Label assignedQuizzesCount;
    @FXML private Label completedQuizzesCount;
    @FXML private Label averageScoreLabel;

    // Assigned Quizzes Table
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, String> quizDescriptionColumn;
    @FXML private TableColumn<Quiz, String> quizQuestionsColumn;
    @FXML private TableColumn<Quiz, String> quizAttemptsColumn;
    @FXML private TableColumn<Quiz, String> quizStatusColumn;
    @FXML private TableColumn<Quiz, String> quizGradeColumn;
    @FXML private TextField quizSearchField;

    // History Table
    @FXML private TableView<QuizAttempt> historyTable;
    @FXML private TableColumn<QuizAttempt, String> historyQuizColumn;
    @FXML private TableColumn<QuizAttempt, String> historyScoreColumn;
    @FXML private TableColumn<QuizAttempt, String> historyDateColumn;

    // Quiz-specific analytics section
    @FXML private VBox quizAnalyticsSection;
    @FXML private Label selectedQuizLabel;
    @FXML private Label quizStatsLabel;
    @FXML private Label quizChartMessageLabel;
    @FXML private LineChart<String, Number> quizSpecificChart;
    @FXML private NumberAxis quizYAxis;

    // Services
    private final QuizService quizService = ServiceLocator.getQuizService();
    private final AttemptService attemptService = ServiceLocator.getAttemptService();
    private final com.example.service.GroupService groupService = ServiceLocator.getGroupService();

    // Data
    private ObservableList<Quiz> assignedQuizzes;
    private FilteredList<Quiz> filteredQuizzes;
    private User currentUser;
    @FXML private ComboBox<Group> studentGroupCombo;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupHeader();
        setupQuizzesTable();
        setupHistoryTable();
        setupSearch();
        setupQuizSpecificChart();
        setupTableSelectionListeners();
        loadData();
    }

    private void setupHeader() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome back, " + currentUser.getFirstName() + "!");
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

        // NEW: Attempts column
        quizAttemptsColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            int attemptCount = attemptService.getStudentAttemptsForQuiz(currentUser.getId(), quiz.getId()).size();
            return new SimpleStringProperty(String.valueOf(attemptCount));
        });

        // UPDATED: Status column now shows score as fraction (e.g., "Completed (3/3pts)")
        quizStatusColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            boolean attempted = attemptService.hasStudentAttemptedQuiz(currentUser.getId(), quiz.getId());

            if (attempted) {
                Optional<Integer> bestScore = attemptService.getBestScore(currentUser.getId(), quiz.getId());
                if (bestScore.isPresent()) {
                    int totalScore = quiz.getQuestions().stream()
                            .mapToInt(Question::getAssignedScore)
                            .sum();
                    String scoreText = String.format("Completed (%d/%d pts)", bestScore.get(), totalScore);
                    return new SimpleStringProperty(scoreText);
                } else {
                    return new SimpleStringProperty("Completed");
                }
            }
            return new SimpleStringProperty("Not started");
        });

        // Style the status column
        quizStatusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("Completed")) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #6c757d;");
                    }
                }
            }
        });

        // NEW: Grade column showing percentage
        quizGradeColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            Optional<Integer> bestScore = attemptService.getBestScore(currentUser.getId(), quiz.getId());

            if (!bestScore.isPresent()) {
                return new SimpleStringProperty("N/A");
            }

            int totalScore = quiz.getQuestions().stream()
                    .mapToInt(Question::getAssignedScore)
                    .sum();

            if (totalScore == 0) {
                return new SimpleStringProperty("N/A");
            }

            double percentage = (bestScore.get() * 100.0) / totalScore;
            return new SimpleStringProperty(String.format("%.1f%%", percentage));
        });

        // Color code the grade column
        quizGradeColumn.setCellFactory(column -> new TableCell<>() {
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

    private void setupHistoryTable() {
        historyQuizColumn.setCellValueFactory(cellData -> {
            int quizId = cellData.getValue().getQuizId();
            String title = quizService.getQuizById(quizId)
                    .map(Quiz::getTitle)
                    .orElse("Unknown Quiz");
            return new SimpleStringProperty(title);
        });

        // UPDATED: Show score as fraction (e.g., "3/3 pts")
        historyScoreColumn.setCellValueFactory(cellData -> {
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

        historyDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            return new SimpleStringProperty(cellData.getValue().getAttemptedAt().format(fmt));
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
            // Update assigned count after filtering
            updateAssignedQuizzesCount();
        });
    }

    private void setupGroupCombo() {
        studentGroupCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
        studentGroupCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        studentGroupCombo.setOnAction(e -> {
            Group sel = studentGroupCombo.getValue();
            if (sel == null) {
                // show all assigned quizzes
                filteredQuizzes.setPredicate(q -> true);
            } else {
                filteredQuizzes.setPredicate(q -> q.getGroupId() == sel.getId());
            }
            // Update assigned count label after filtering
            updateAssignedQuizzesCount();
        });
    }

    private void setupQuizSpecificChart() {
        quizYAxis.setAutoRanging(false);
        quizYAxis.setLowerBound(0);
        quizYAxis.setUpperBound(100);
        quizYAxis.setTickUnit(10);

        quizSpecificChart.setLegendVisible(false);
        quizSpecificChart.setCreateSymbols(true);
        quizSpecificChart.setAnimated(true);
    }

    // Setup selection listeners for both tables
    private void setupTableSelectionListeners() {
        // Listen to quizzes table selection
        quizzesTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    historyTable.getSelectionModel().clearSelection();
                    showQuizAnalytics(newValue.getId());
                }
            }
        );

        // Listen to history table selection
        historyTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    quizzesTable.getSelectionModel().clearSelection();
                    showQuizAnalytics(newValue.getQuizId());
                }
            }
        );
    }

    // FIXED: Show analytics for a specific quiz with correct x-axis alignment and improvement calculation
    private void showQuizAnalytics(int quizId) {
        try (Connection conn = DatabaseManager.connectWithDatabase()) {
            if (conn == null) return;

            // Get quiz details
            Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
            if (!quizOpt.isPresent()) return;

            Quiz quiz = quizOpt.get();

            // Get attempts for this specific quiz
            Map<Integer, QuizAttempt> attemptsMap = RetrieveFromDatabase.getScores(conn, quizId, currentUser.getId());

            if (attemptsMap.isEmpty()) {
                selectedQuizLabel.setText("Quiz: " + quiz.getTitle());
                quizStatsLabel.setText("No attempts yet for this quiz");
                quizChartMessageLabel.setText("💡 Take this quiz to see your progress!");
                quizAnalyticsSection.setVisible(true);
                quizAnalyticsSection.setManaged(true);
                quizSpecificChart.getData().clear();
                return;
            }

            // Convert map to sorted list by attempt number
            List<QuizAttempt> quizAttempts = new ArrayList<>(attemptsMap.values());
            quizAttempts.sort(Comparator.comparingInt(QuizAttempt::getAttemptNumber));

            // Update labels
            selectedQuizLabel.setText("Quiz: " + quiz.getTitle());

            // Calculate total possible score
            int totalPossibleScore = quiz.getQuestions().stream()
                    .mapToInt(Question::getAssignedScore)
                    .sum();

            if (totalPossibleScore == 0) {
                quizStatsLabel.setText("Quiz has no questions or invalid scoring");
                quizChartMessageLabel.setText("");
                quizAnalyticsSection.setVisible(true);
                quizAnalyticsSection.setManaged(true);
                quizSpecificChart.getData().clear();
                return;
            }

            // Calculate percentages for each attempt
            List<Double> percentages = new ArrayList<>();
            for (QuizAttempt attempt : quizAttempts) {
                double percentage = (attempt.getTotalScore() * 100.0) / totalPossibleScore;
                percentages.add(percentage);
            }

            double bestScore = percentages.stream().max(Double::compare).orElse(0.0);
            int totalAttempts = quizAttempts.size();

            quizStatsLabel.setText(String.format(
                "Best Score: %.1f%% | Total Attempts: %d | Questions: %d",
                bestScore, totalAttempts, quiz.getQuestions().size()
            ));

            // Build the chart with FIXED x-axis alignment
            quizSpecificChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(quiz.getTitle());

            for (int i = 0; i < percentages.size(); i++) {
                String attemptLabel = "Attempt " + (i + 1);
                series.getData().add(new XYChart.Data<>(attemptLabel, percentages.get(i)));
            }

            quizSpecificChart.getData().add(series);

            // FIXED: Calculate improvement correctly
            if (percentages.size() >= 2) {
                double firstScore = percentages.get(0);
                double lastScore = percentages.get(percentages.size() - 1);
                double improvement = lastScore - firstScore;  // Fixed: was multiplied by 100 incorrectly

                String trend = improvement > 0 ? "📈 Improving" :
                              improvement < 0 ? "📉 Declining" : "➡️ Stable";

                quizChartMessageLabel.setText(String.format(
                    "%s | Improvement: %+.1f%% from first attempt",
                    trend, improvement
                ));
                quizChartMessageLabel.setStyle(improvement >= 0 ?
                    "-fx-text-fill: #28a745;" : "-fx-text-fill: #dc3545;");
            } else {
                quizChartMessageLabel.setText("📊 Complete more attempts to track improvement");
                quizChartMessageLabel.setStyle("-fx-text-fill: #6c757d;");
            }

            // Show the analytics section
            quizAnalyticsSection.setVisible(true);
            quizAnalyticsSection.setManaged(true);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load quiz analytics: " + e.getMessage());
        }
    }

    @FXML
    private void handleCloseAnalytics() {
        quizAnalyticsSection.setVisible(false);
        quizAnalyticsSection.setManaged(false);
        quizzesTable.getSelectionModel().clearSelection();
        historyTable.getSelectionModel().clearSelection();
    }

    private void loadData() {
        if (currentUser == null) return;

        // Load assigned quizzes
        List<Quiz> quizzes = quizService.getAssignedQuizzes(currentUser.getId());
        assignedQuizzes = FXCollections.observableArrayList(quizzes);
        filteredQuizzes = new FilteredList<>(assignedQuizzes, p -> true);
        quizzesTable.setItems(filteredQuizzes);

        // Load attempt history
        List<QuizAttempt> attempts = attemptService.getAttemptsByStudent(currentUser.getId());
        historyTable.setItems(FXCollections.observableArrayList(attempts));

        // Load groups for this student
        try {
            List<com.example.model.Group> groups = groupService.getGroupsByStudent(currentUser.getId());
            if (groups == null || groups.isEmpty()) {
                groupsLabel.setText("Groups: None");
                studentGroupCombo.setItems(FXCollections.observableArrayList());
            } else {
                String names = String.join(", ", groups.stream().map(com.example.model.Group::getName).toArray(String[]::new));
                groupsLabel.setText("Groups: " + names);
                studentGroupCombo.setItems(FXCollections.observableArrayList(groups));
                studentGroupCombo.getItems().add(0, null); // option to show all
                studentGroupCombo.setValue(null);
                setupGroupCombo();
            }
        } catch (Exception e) {
            groupsLabel.setText("Groups: Error loading groups");
        }

        // Update stats
        updateStats(quizzes, attempts);
    }

    // UPDATED: Calculate stats with correct logic
    private void updateStats(List<Quiz> quizzes, List<QuizAttempt> attempts) {
        // Count unique completed quizzes
        long completedCount = attempts.stream()
                .map(QuizAttempt::getQuizId)
                .distinct()
                .count();
        completedQuizzesCount.setText(String.valueOf(completedCount));

        // FIXED: Assigned quizzes count should exclude completed ones
        updateAssignedQuizzesCount();

        // FIXED: Calculate average as percentage of best scores only
        if (completedCount == 0) {
            averageScoreLabel.setText("N/A");
        } else {
            // Get best score for each completed quiz
            List<Double> bestPercentages = new ArrayList<>();

            // Get unique quiz IDs that student has attempted
            List<Integer> attemptedQuizIds = attempts.stream()
                    .map(QuizAttempt::getQuizId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            for (Integer quizId : attemptedQuizIds) {
                Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
                if (quizOpt.isPresent()) {
                    Quiz quiz = quizOpt.get();
                    Optional<Integer> bestScore = attemptService.getBestScore(currentUser.getId(), quizId);

                    if (bestScore.isPresent()) {
                        int totalScore = quiz.getQuestions().stream()
                                .mapToInt(Question::getAssignedScore)
                                .sum();
                        if (totalScore > 0) {
                            double percentage = (bestScore.get() * 100.0) / totalScore;
                            bestPercentages.add(percentage);
                        }
                    }
                }
            }

            if (bestPercentages.isEmpty()) {
                averageScoreLabel.setText("N/A");
            } else {
                double avgPercentage = bestPercentages.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
                averageScoreLabel.setText(String.format("%.1f%%", avgPercentage));
            }
        }
    }

    // Helper method to update assigned quizzes count
    private void updateAssignedQuizzesCount() {
        // Count quizzes that are NOT completed (i.e., have zero attempts)
        long incompleteCount = filteredQuizzes.stream()
                .filter(quiz -> {
                    List<QuizAttempt> quizAttempts = attemptService.getStudentAttemptsForQuiz(
                            currentUser.getId(), quiz.getId());
                    return quizAttempts.isEmpty();
                })
                .count();
        assignedQuizzesCount.setText(String.valueOf(incompleteCount));
    }

    @FXML
    private void handleStartQuiz() {
        Quiz selectedQuiz = quizzesTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert("No Quiz Selected", "Please select a quiz from the table to start.");
            return;
        }

        // Check if quiz has questions
        if (selectedQuiz.getQuestionCount() == 0) {
            showAlert("Quiz Not Ready", "This quiz has no questions yet. Please try another quiz.");
            return;
        }

        // Navigate to quiz taking screen
        QuizTakeController controller = SceneManager.getInstance()
                .loadViewWithController(SceneManager.QUIZ_TAKE);
        if (controller != null) {
            controller.setQuiz(selectedQuiz);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
