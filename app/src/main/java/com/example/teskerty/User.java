package com.example.teskerty;

public class User {
    private String email, password, name, lastName, role;

    // Constructor
    public User(String name, String lastName, String email, String password, String role) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getter methods for user data
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
    public String getLastName() {
        return lastName;
    }
    public String getRole() {
        return role;
    }

    public void setEmail(String email) {this.email = email;}
    public void setName(String name) {this.name = name;}
    public void setPassword(String password) {this.password = password;}

    public void setLastName(String lastName) {this.lastName = lastName;}


}
