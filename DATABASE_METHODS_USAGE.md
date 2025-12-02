# Database Methods Usage Guide

This document explains when and how to use each database insertion method.

## InsertIntoDatabase Methods

### 1. insertPeople()
**When to trigger:** When admin creates a new user

**Method signature:**
```java
public static void insertPeople(Connection conn, String name, String lastName, String email, String password, String role)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();
InsertIntoDatabase.insertPeople(conn, "John", "Doe", "john@example.com", "password123", "student");
```

---

### 2. insertGroup()
**When to trigger:** When teacher creates a group

**Required logic:** Automatically looks up teacher ID by email

**Method signatures:**
```java
// Using teacher email (recommended)
public static void insertGroup(Connection conn, String groupName, String teacherEmail)

// Using teacher ID directly
public static void insertGroup(Connection conn, String groupName, int teacherId)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();
InsertIntoDatabase.insertGroup(conn, "Java Class 2025", "teacher@example.com");
```

---

### 3. insertEnrollment()
**When to trigger:** When student is assigned to a group

**Required logic:**
- Automatically looks up group ID by group name
- Automatically looks up student ID by student email

**Method signatures:**
```java
// Using group name and student email (recommended)
public static void insertEnrollment(Connection conn, String groupName, String studentEmail)

// Using IDs directly
public static void insertEnrollment(Connection conn, int groupId, int studentId)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();
InsertIntoDatabase.insertEnrollment(conn, "Java Class 2025", "student@example.com");
```

---

### 4. insertQuiz()
**When to trigger:** When quiz pack is created

**Required logic:** Automatically looks up group ID by group name

**Returns:** The ID of the newly created quiz (needed for insertQuizQuestion)

**Method signatures:**
```java
// Using group name (recommended)
public static int insertQuiz(Connection conn, String quizName, String description, String groupName)

// Using group ID directly
public static int insertQuiz(Connection conn, String quizName, String description, int groupId)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();
int quizId = InsertIntoDatabase.insertQuiz(conn, "Week 1 Quiz", "Introduction to Java", "Java Class 2025");
// Store quizId for later use with insertQuizQuestion()
```

---

### 5. insertMcq()
**When to trigger:** When a multiple choice question is created

**Returns:** The ID of the newly created question (needed for insertQuizQuestion)

**Method signature:**
```java
public static int insertMcq(Connection conn, String question, String optionA, String optionB,
                            String optionC, String optionD, char correctOption, int assignedScore)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();
int questionId = InsertIntoDatabase.insertMcq(
    conn,
    "What is Java?",
    "A coffee",
    "A programming language",
    "An island",
    "A car brand",
    'B',  // correct option
    10    // points for this question
);
// Store questionId for later use with insertQuizQuestion()
```

---

### 6. insertQuizQuestion()
**When to trigger:** After creating both quiz and question to link them together

**Required logic:**
- Needs quiz ID from insertQuiz()
- Needs question ID from insertMcq()

**Method signature:**
```java
public static void insertQuizQuestion(Connection conn, int quizId, int questionId)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();

// Step 1: Create quiz
int quizId = InsertIntoDatabase.insertQuiz(conn, "Week 1 Quiz", "Intro to Java", "Java Class 2025");

// Step 2: Create question
int questionId = InsertIntoDatabase.insertMcq(conn, "What is Java?", "Coffee", "Language", "Island", "Brand", 'B', 10);

// Step 3: Link question to quiz
InsertIntoDatabase.insertQuizQuestion(conn, quizId, questionId);
```

---

### 7. insertScore()
**When to trigger:** When student submits quiz and final score needs to be recorded

**Required logic:**
- Automatically looks up quiz ID by quiz name
- Automatically looks up student ID by student email
- Automatically tracks attempt number (increments from previous attempts)

**Method signatures:**
```java
// Using quiz name and student email (recommended)
public static void insertScore(Connection conn, String quizName, String studentEmail, int score)

// Using IDs and attempt number directly
public static void insertScore(Connection conn, int quizId, int studentId, int attempt, int score)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();

// Calculate total score from all questions first
int totalScore = 85;

// Insert the score (attempt number is automatically calculated)
InsertIntoDatabase.insertScore(conn, "Week 1 Quiz", "student@example.com", totalScore);
```

---

### 8. insertStudentAnswer()
**When to trigger:** When student submits an answer to a question

**Required logic:**
- Automatically looks up student ID by email
- Automatically checks if answer is correct by comparing with correct option
- Automatically retrieves assigned score for the question
- Automatically calculates score (full points if correct, 0 if incorrect)
- Automatically records current date

**Method signatures:**
```java
// Using student email (recommended - handles all calculations)
public static void insertStudentAnswer(Connection conn, int questionId, String studentEmail,
                                      int attempt, char selectedOption)

// Using student ID (handles all calculations)
public static void insertStudentAnswer(Connection conn, int questionId, int studentId,
                                      int attempt, char selectedOption)

// Manual mode (for backward compatibility)
public static void insertStudentAnswer(Connection conn, int questionId, int studentId, int attempt,
                                      char selectedOption, boolean isCorrect, int score, LocalDate date)
```

**Example usage:**
```java
Connection conn = DatabaseManager.connectWithDatabase();

// When student answers a question, just provide the question ID, email, attempt, and their choice
InsertIntoDatabase.insertStudentAnswer(conn, questionId, "student@example.com", 1, 'B');

// The method will:
// 1. Get the correct answer from the database
// 2. Compare student's answer with correct answer
// 3. Calculate score (10 points if correct, 0 if wrong)
// 4. Record current date
// 5. Insert everything into the database
```

---

## Complete Quiz Workflow Example

Here's a complete example showing how these methods work together:

```java
import com.example.database.DatabaseManager;
import com.example.database.InsertIntoDatabase;
import java.sql.Connection;

public class QuizWorkflowExample {
    public static void main(String[] args) throws SQLException {
        Connection conn = DatabaseManager.connectWithDatabase();

        // 1. Admin creates users
        InsertIntoDatabase.insertPeople(conn, "Jane", "Smith", "teacher@school.com", "pass123", "teacher");
        InsertIntoDatabase.insertPeople(conn, "Alice", "Johnson", "alice@school.com", "pass456", "student");

        // 2. Teacher creates a group
        InsertIntoDatabase.insertGroup(conn, "Programming 101", "teacher@school.com");

        // 3. Admin/Teacher assigns student to group
        InsertIntoDatabase.insertEnrollment(conn, "Programming 101", "alice@school.com");

        // 4. Teacher creates a quiz
        int quizId = InsertIntoDatabase.insertQuiz(conn, "Java Basics", "Test your Java knowledge", "Programming 101");

        // 5. Teacher adds questions
        int q1Id = InsertIntoDatabase.insertMcq(conn, "What is Java?", "Coffee", "Language", "Island", "Brand", 'B', 10);
        int q2Id = InsertIntoDatabase.insertMcq(conn, "Is Java OOP?", "Yes", "No", "Maybe", "Sometimes", 'A', 10);

        // 6. Link questions to quiz
        InsertIntoDatabase.insertQuizQuestion(conn, quizId, q1Id);
        InsertIntoDatabase.insertQuizQuestion(conn, quizId, q2Id);

        // 7. Student takes the quiz (attempt 1)
        InsertIntoDatabase.insertStudentAnswer(conn, q1Id, "alice@school.com", 1, 'B'); // Correct
        InsertIntoDatabase.insertStudentAnswer(conn, q2Id, "alice@school.com", 1, 'A'); // Correct

        // 8. Record final score
        InsertIntoDatabase.insertScore(conn, "Java Basics", "alice@school.com", 20); // 20/20 points

        System.out.println("Quiz workflow completed successfully!");
    }
}
```

## RetrieveFromDatabase Helper Methods

These methods are used internally by InsertIntoDatabase but can also be called directly:

- `getTeacherId(Connection conn, String teacherEmail)` - Get teacher ID by email
- `getGroupId(Connection conn, String groupName)` - Get group ID by name
- `getStudentId(Connection conn, String studentEmail)` - Get student ID by email
- `getQuizId(Connection conn, String quizName)` - Get quiz ID by name
- `getQuestionIdByText(Connection conn, String questionText)` - Get question ID by text
- `getNextAttemptNumber(Connection conn, int quizId, int studentId)` - Get next attempt number
- `getCorrectOption(Connection conn, int questionId)` - Get correct answer for question
- `getAssignedScore(Connection conn, int questionId)` - Get points for question

All helper methods are public and throw SQLException if the entity is not found.
