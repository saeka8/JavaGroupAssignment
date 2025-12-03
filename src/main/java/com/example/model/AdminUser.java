package com.example.model;

public class AdminUser extends User {
    public AdminUser(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName, Role.ADMIN);
    }

    public AdminUser(int id, String email, String password, String firstName, String lastName) {
        super(id, email, password, firstName, lastName, Role.ADMIN);
    }
}
