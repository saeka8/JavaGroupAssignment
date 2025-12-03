package com.example.model;

/**
 * Represents a group in the Quiz Platform.
 * A group contains students and is assigned to one teacher.
 */
public class Group {

    private int id;
    private String name;
    private int teacherId;

    // Default constructor
    public Group() {}

    // Full constructor
    public Group(int id, String name, int teacherId) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
    }

    // Constructor without ID (for new groups before DB insertion)
    public Group(String name, int teacherId) {
        this.name = name;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}
