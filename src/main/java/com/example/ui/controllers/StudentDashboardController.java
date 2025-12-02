package com.example.ui.controllers;

import com.example.database.DatabaseManager; 
import com.example.database.RetrieveFromDatabase; 
import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Analytics;
import com.example.quizlogic.QuizAttempt;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;


import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
/**
 * Controller for the Student Dashboard.
 * Shows assigned quizzes and attempt history.
 */
public class StudentDashboardController {

    // Header
    @FXML private Label welcomeLabel;
    @FXML private Label userEmailLabel;

    // Stats
    @FXML private Label assignedQuizzesCount;
    @FXML private Label completedQuizzesCount;
    @FXML private Label averageScoreLabel;

    // Assigned Quizzes Table
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, String> quizDescriptionColumn;
    @FXML private TableColumn<Quiz, String> quizQuestionsColumn;
    @FXML private TableColumn<Quiz, String> quizStatusColumn;
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

    // Data
    private ObservableList<Quiz> assignedQuizzes;
    private FilteredList<Quiz> filteredQuizzes;
    private User currentUser;

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
            return new SimpleStringProperty(count + " questions");
        });

        quizStatusColumn.setCellValueFactory(cellData -> {
        Quiz quiz = cellData.getValue();
        boolean attempted = attemptService.hasStudentAttemptedQuiz(currentUser.getId(), quiz.getId());
        if (attempted) {
            Optional<Integer> bestScore = attemptService.getBestScore(currentUser.getId(), quiz.getId());
            String scoreText = bestScore.map(s -> "Completed (" + s + " pts)").orElse("Completed");
            return new SimpleStringProperty(scoreText);

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
    }

    private void setupHistoryTable() {
        historyQuizColumn.setCellValueFactory(cellData -> {
            int quizId = cellData.getValue().getQuizId();
            String title = quizService.getQuizById(quizId)
                    .map(Quiz::getTitle)
                    .orElse("Unknown Quiz");
            return new SimpleStringProperty(title);
        });

    historyScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotalScore() + " pts"));   // NEW

        historyDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
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

    // Show analytics for a specific quiz
    private void showQuizAnalytics(int quizId) {
        try (Connection conn = DatabaseManager.connectWithDatabase()) {
            if (conn == null) return;

            // Get quiz details
            Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
            if (!quizOpt.isPresent()) return;
            
            Quiz quiz = quizOpt.get();
            
            // Get attempts for this specific quiz using the new SQL method
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
            
            // Calculate stats using Analytics
            List<Integer> scores = Analytics.getScoresOverTime(quizAttempts);
            int totalQuestions = quiz.getQuestions().size();
            List<Double> percentages = Analytics.convertToPercentages(scores, totalQuestions);
            
            double bestScore = percentages.stream().max(Double::compare).orElse(0.0);
            int totalAttempts = quizAttempts.size();
            
            quizStatsLabel.setText(String.format(
                "Best Score: %.1f%% | Total Attempts: %d | Questions: %d", 
                bestScore, totalAttempts, totalQuestions
            ));

            // Build the chart
            quizSpecificChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(quiz.getTitle());

            for (int i = 0; i < percentages.size(); i++) {
                String attemptLabel = "Attempt " + (i + 1);
                series.getData().add(new XYChart.Data<>(attemptLabel, percentages.get(i)));
            }

            quizSpecificChart.getData().add(series);

            // Calculate improvement
            if (percentages.size() >= 2) {
                double firstScore = percentages.get(0);
                double lastScore = percentages.get(percentages.size() - 1);
                double improvement = lastScore - firstScore;
                
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

        // Update stats
        updateStats(quizzes, attempts);
    }


    private void updateStats(List<Quiz> quizzes, List<QuizAttempt> attempts) {
        assignedQuizzesCount.setText(String.valueOf(quizzes.size()));

        // Count unique completed quizzes
        long completedCount = attempts.stream()
                .map(QuizAttempt::getQuizId)
                .distinct()
                .count();
        completedQuizzesCount.setText(String.valueOf(completedCount));

        // Calculate average using existing Analytics approach
        if (attempts.isEmpty()) {
            averageScoreLabel.setText("N/A");
        } else {
            // Get all scores
            List<Integer> allScores = Analytics.getScoresOverTime(attempts);
            
            // Calculate average - assuming each quiz has similar question count
            // For more accuracy, you'd need to weight by question count
            double avgScore = allScores.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            
            // Estimate percentage (you might want to store max score per attempt)
            averageScoreLabel.setText(String.format("%.1f", avgScore));
        }
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
