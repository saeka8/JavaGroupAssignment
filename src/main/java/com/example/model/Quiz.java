package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.quizlogic.Question;

/**
 * Represents a Quiz created by a teacher.
 */
public class Quiz {
    
    private int id;
    private String title;
    private String description;
    private int teacherId;
    private String teacherName; // For display purposes
    private LocalDateTime createdAt;
    private boolean isActive;
    private List<Question> questions;
    
    public Quiz() {
        this.questions = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public Quiz(int id, String title, String description, int teacherId) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.teacherId = teacherId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    public void addQuestion(Question question) {
        this.questions.add(question);
    }
    
    public int getQuestionCount() {
        return questions.size();
    }
    
    @Override
    public String toString() {
        return title; // Useful for ComboBox/ListView display
    }
}
