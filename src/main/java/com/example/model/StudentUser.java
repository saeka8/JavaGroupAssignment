package com.example.model;

public class StudentUser extends User {

    public StudentUser(int id, String email, String password, String firstName, String lastName) {
        super(id, email, password, firstName, lastName, Role.STUDENT);
    }
}
