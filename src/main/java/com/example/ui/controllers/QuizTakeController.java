package com.example.ui.controllers;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;
import com.example.quizlogic.QuizAttempt;
import com.example.quizlogic.QuizGrader;
import com.example.quizlogic.QuizTakingSession;
import com.example.service.AttemptService;
import com.example.service.QuizService;
import com.example.service.ServiceLocator;
import com.example.ui.util.SceneManager;
import com.example.ui.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Controller for the Quiz Taking screen.
 * Handles displaying questions and collecting answers.
 */
public class QuizTakeController {

    // Header
    @FXML
    private Label quizTitleLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;

    // Question display
    @FXML
    private Label questionNumberLabel;
    @FXML
    private Label questionTextLabel;

    // Answer options
    @FXML
    private VBox optionsContainer;
    @FXML
    private ToggleGroup optionsGroup;

    // Navigation
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button submitButton;

    // Services
    private final QuizService quizService = ServiceLocator.getQuizService();
    private final AttemptService attemptService = ServiceLocator.getAttemptService();

    // State
    private Quiz quiz;
    private QuizTakingSession session;
    private User currentUser;

    // Option buttons (for styling)
    private ToggleButton optionA, optionB, optionC, optionD;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        optionsGroup = new ToggleGroup();
        createOptionButtons();
    }

    /**
     * Set the quiz to take. Called by the previous screen.
     */
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;

        // Load questions if not already loaded
        if (quiz.getQuestions().isEmpty()) {
            quiz.setQuestions(quizService.getQuestionsByQuiz(quiz.getId()));
        }

        // Initialize session
        this.session = new QuizTakingSession(quiz.getQuestions());

        // Setup UI
        quizTitleLabel.setText(quiz.getTitle());
        updateQuestion();
    }

    private void createOptionButtons() {
        optionsContainer.getChildren().clear();
        optionsContainer.setSpacing(12);

        optionA = createOptionButton("A");
        optionB = createOptionButton("B");
        optionC = createOptionButton("C");
        optionD = createOptionButton("D");

        optionsContainer.getChildren().addAll(optionA, optionB, optionC, optionD);
    }

    private ToggleButton createOptionButton(String letter) {
        ToggleButton btn = new ToggleButton();
        btn.setToggleGroup(optionsGroup);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(15, 20, 15, 20));
        btn.setStyle(getDefaultOptionStyle());
        btn.setUserData(letter.charAt(0));

        // Style changes on selection
        btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                btn.setStyle(getSelectedOptionStyle());
            } else {
                btn.setStyle(getDefaultOptionStyle());
            }
        });

        // Save answer when selected
        btn.setOnAction(e -> {
            if (btn.isSelected()) {
                session.answerCurrentQuestion((Character) btn.getUserData());
            }
        });

        return btn;
    }

    private String getDefaultOptionStyle() {
        return "-fx-background-color: white; " +
                "-fx-border-color: #dee2e6; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand;";
    }

    private String getSelectedOptionStyle() {
        return "-fx-background-color: #e8f0fe; " +
                "-fx-border-color: #2B3A67; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold;";
    }

    private void updateQuestion() {
        if (session == null || quiz == null)
            return;

        Question current = session.getCurrentQuestion();
        int currentIdx = session.getCurrentIndex();
        int total = session.getTotalQuestions();

        // Update progress
        progressLabel.setText("Question " + (currentIdx + 1) + " of " + total);
        progressBar.setProgress((double) (currentIdx + 1) / total);

        // Update question
        questionNumberLabel.setText("Question " + (currentIdx + 1));
        questionTextLabel.setText(current.getText());

        // Update options
        optionA.setText("A.  " + current.getOptionA());
        optionB.setText("B.  " + current.getOptionB());
        optionC.setText("C.  " + current.getOptionC());
        optionD.setText("D.  " + current.getOptionD());

        // Restore previous answer if any
        Character previousAnswer = session.getAnswerForCurrentQuestion();
        optionsGroup.selectToggle(null); // Clear selection first

        if (previousAnswer != null) {
            switch (previousAnswer) {
                case 'A' -> optionsGroup.selectToggle(optionA);
                case 'B' -> optionsGroup.selectToggle(optionB);
                case 'C' -> optionsGroup.selectToggle(optionC);
                case 'D' -> optionsGroup.selectToggle(optionD);
            }
        }

        // Update navigation buttons
        prevButton.setDisable(!session.hasPrevious());

        // Show submit on last question, otherwise show next
        boolean isLast = !session.hasNext();
        nextButton.setVisible(!isLast);
        nextButton.setManaged(!isLast);
        submitButton.setVisible(isLast);
        submitButton.setManaged(isLast);
    }

    @FXML
    private void handlePrevious() {
        session.previous();
        updateQuestion();
    }

    @FXML
    private void handleNext() {
        // Check if current question is answered
        if (session.getAnswerForCurrentQuestion() == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unanswered Question");
            alert.setHeaderText("You haven't answered this question.");
            alert.setContentText("Do you want to skip it and move to the next question?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        session.next();
        updateQuestion();
    }

    @FXML
    private void handleSubmit() {
        // Check for unanswered questions
        int answered = session.getAllAnswers().size();
        int total = session.getTotalQuestions();

        if (answered < total) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Incomplete Quiz");
            alert.setHeaderText("You have " + (total - answered) + " unanswered question(s).");
            alert.setContentText("Are you sure you want to submit? Unanswered questions will be marked as incorrect.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        int attemptNumber = attemptService.getAttemptCount(currentUser.getId(), quiz.getId()) + 1;

        // Grade the quiz
        QuizAttempt attempt = QuizGrader.gradeQuiz(
                currentUser.getId(),
                quiz.getId(),
                attemptNumber,
                quiz.getQuestions(),
                session.getAllAnswers());

        // Save attempt
        attemptService.saveAttempt(attempt);

        // Show results
        showResults(attempt);
    }

    private void showResults(QuizAttempt attempt) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Quiz Complete!");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        int totalScore = attempt.getTotalScore();

        Label emojiLabel = new Label(totalScore >= 80 ? "🎉" : totalScore >= 60 ? "👍" : "📚");
        emojiLabel.setStyle("-fx-font-size: 48px;");

        Label scoreLabel = new Label(totalScore + " pts");
        scoreLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        int correct = (int) attempt.getAnswers().stream().filter(a -> a.isCorrect()).count();
        int total = attempt.getAnswers().size();

        Label statsLabel = new Label("Correct: " + correct + "/" + total);
        statsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        content.getChildren().addAll(emojiLabel, scoreLabel, statsLabel);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.setOnHidden(e -> SceneManager.getInstance().switchScene(SceneManager.STUDENT_DASHBOARD));

        dialog.showAndWait();
    }

    private String getScoreMessage(int score) {
        if (score >= 90)
            return "Excellent work! You've mastered this material!";
        if (score >= 80)
            return "Great job! You're doing really well!";
        if (score >= 70)
            return "Good effort! Keep practicing!";
        if (score >= 60)
            return "Not bad! Review the material and try again.";
        return "Keep studying! You'll get better with practice.";
    }

    @FXML
    private void handleQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Quiz");
        alert.setHeaderText("Are you sure you want to quit?");
        alert.setContentText("Your progress will not be saved.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SceneManager.getInstance().switchScene(SceneManager.STUDENT_DASHBOARD);
        }
    }
}
