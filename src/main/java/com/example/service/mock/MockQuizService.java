package com.example.service.mock;

import com.example.model.Quiz;
import com.example.model.User;
import com.example.quizlogic.Question;
import com.example.service.QuizService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of QuizService for UI testing.
 * Replace with real implementation when Person 3 (Silvia) completes their work.
 */
public class MockQuizService implements QuizService {
    
    private final Map<Integer, Quiz> quizzes = new HashMap<>();
    private final Map<Integer, Question> questions = new HashMap<>();
    private final Map<Integer, Set<Integer>> quizAssignments = new HashMap<>(); // quizId -> studentIds
    
    private int nextQuizId = 1;
    private int nextQuestionId = 1;
    
    // Reference to UserService to get student info
    private final MockUserService userService;
    
    public MockQuizService(MockUserService userService) {
        this.userService = userService;
        initializeTestData();
    }
    
    private void initializeTestData() {
        // Create sample quizzes
        Quiz quiz1 = new Quiz(nextQuizId++, "Java Basics", "Test your knowledge of Java fundamentals", 2);
        quiz1.setTeacherName("John Smith");
        
        Quiz quiz2 = new Quiz(nextQuizId++, "OOP Concepts", "Object-Oriented Programming principles", 2);
        quiz2.setTeacherName("John Smith");
        
        Quiz quiz3 = new Quiz(nextQuizId++, "Data Structures", "Arrays, Lists, Maps and more", 3);
        quiz3.setTeacherName("Sarah Johnson");
        
        // Add questions to quiz1
        Question q1 = new Question(nextQuestionId++, "What is the correct way to declare a variable in Java?", 
                "var x = 5", "int x = 5;", "x := 5", "declare x = 5", 'B', 1);
        Question q2 = new Question(nextQuestionId++, "Which keyword is used to create a class in Java?", 
                "struct", "class", "def", "function", 'B', 1);
        Question q3 = new Question(nextQuestionId++, "What is the output of: System.out.println(5 + 3);", 
                "53", "8", "5 + 3", "Error", 'B', 1);
        
        quiz1.addQuestion(q1);
        quiz1.addQuestion(q2);
        quiz1.addQuestion(q3);
        
        questions.put(q1.getId(), q1);
        questions.put(q2.getId(), q2);
        questions.put(q3.getId(), q3);
        
        // Add questions to quiz2
        Question q4 = new Question(nextQuestionId++, "What is encapsulation?", 
                "Hiding implementation details", "Creating objects", "Inheriting properties", "None of the above", 'A', 1);
        Question q5 = new Question(nextQuestionId++, "Which OOP principle allows a class to inherit from another?", 
                "Polymorphism", "Encapsulation", "Inheritance", "Abstraction", 'C', 1);
        
        quiz2.addQuestion(q4);
        quiz2.addQuestion(q5);
        
        questions.put(q4.getId(), q4);
        questions.put(q5.getId(), q5);
        
        // Store quizzes
        quizzes.put(quiz1.getId(), quiz1);
        quizzes.put(quiz2.getId(), quiz2);
        quizzes.put(quiz3.getId(), quiz3);
        
        // Assign quizzes to students (student IDs 4, 5, 6, 7, 8)
        assignQuizToStudents(1, Arrays.asList(4, 5, 6));
        assignQuizToStudents(2, Arrays.asList(4, 5, 6, 7, 8));
    }
    
    @Override
    public Quiz createQuiz(Quiz quiz) {
        quiz.setId(nextQuizId++);
        quizzes.put(quiz.getId(), quiz);
        return quiz;
    }
    
    @Override
    public boolean updateQuiz(Quiz quiz) {
        if (quizzes.containsKey(quiz.getId())) {
            quizzes.put(quiz.getId(), quiz);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean deleteQuiz(int quizId) {
        quizAssignments.remove(quizId);
        return quizzes.remove(quizId) != null;
    }
    
    @Override
    public Optional<Quiz> getQuizById(int quizId) {
        return Optional.ofNullable(quizzes.get(quizId));
    }
    
    @Override
    public List<Quiz> getAllQuizzes() {
        return new ArrayList<>(quizzes.values());
    }
    
    @Override
    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        return quizzes.values().stream()
                .filter(q -> q.getTeacherId() == teacherId)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Quiz> searchQuizzes(String query) {
        String lowerQuery = query.toLowerCase();
        return quizzes.values().stream()
                .filter(q -> q.getTitle().toLowerCase().contains(lowerQuery) ||
                            q.getDescription().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    
    @Override
    public Question addQuestion(int quizId, Question question) {
        Quiz quiz = quizzes.get(quizId);
        if (quiz != null) {
            // Create new question with ID
            Question newQ = new Question(nextQuestionId++, question.getText(),
                    question.getOptionA(), question.getOptionB(),
                    question.getOptionC(), question.getOptionD(),
                    question.getCorrectAnswer(), question.getAssignedScore());
            quiz.addQuestion(newQ);
            questions.put(newQ.getId(), newQ);
            return newQ;
        }
        return null;
    }
    
    @Override
    public boolean updateQuestion(Question question) {
        if (questions.containsKey(question.getId())) {
            questions.put(question.getId(), question);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean deleteQuestion(int questionId) {
        Question q = questions.remove(questionId);
        if (q != null) {
            // Remove from quiz
            for (Quiz quiz : quizzes.values()) {
                quiz.getQuestions().removeIf(question -> question.getId() == questionId);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public List<Question> getQuestionsByQuiz(int quizId) {
        Quiz quiz = quizzes.get(quizId);
        return quiz != null ? quiz.getQuestions() : new ArrayList<>();
    }
    
    @Override
    public boolean assignQuizToStudent(int quizId, int studentId) {
        quizAssignments.computeIfAbsent(quizId, k -> new HashSet<>()).add(studentId);
        return true;
    }
    
    @Override
    public boolean assignQuizToStudents(int quizId, List<Integer> studentIds) {
        Set<Integer> assigned = quizAssignments.computeIfAbsent(quizId, k -> new HashSet<>());
        assigned.addAll(studentIds);
        return true;
    }
    
    @Override
    public List<Quiz> getAssignedQuizzes(int studentId) {
        List<Quiz> assigned = new ArrayList<>();
        for (Map.Entry<Integer, Set<Integer>> entry : quizAssignments.entrySet()) {
            if (entry.getValue().contains(studentId)) {
                Quiz quiz = quizzes.get(entry.getKey());
                if (quiz != null) {
                    assigned.add(quiz);
                }
            }
        }
        return assigned;
    }
    
    @Override
    public List<User> getStudentsAssignedToQuiz(int quizId) {
        Set<Integer> studentIds = quizAssignments.getOrDefault(quizId, new HashSet<>());
        return studentIds.stream()
                .map(id -> userService.getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isQuizAssigned(int quizId, int studentId) {
        return quizAssignments.getOrDefault(quizId, new HashSet<>()).contains(studentId);
    }
    
    @Override
    public int getTotalQuizCount() {
        return quizzes.size();
    }
    
    @Override
    public int getActiveQuizCount() {
        return (int) quizzes.values().stream().filter(Quiz::isActive).count();
    }
}
