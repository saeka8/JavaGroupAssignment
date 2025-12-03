package com.example.model;

public class TeacherUser extends User {

    public TeacherUser(int id, String email, String password, String firstName, String lastName) {
        super(id, email, password, firstName, lastName, Role.TEACHER);
    }
}
