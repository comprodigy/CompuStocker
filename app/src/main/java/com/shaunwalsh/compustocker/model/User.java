package com.shaunwalsh.compustocker.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role;

    public User() {}

    public User(String username, String password, String fullName)
    {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String pass) {this.password = pass;}

    public String getFullName() {return fullName;}
    public void setFullName(String fullname) {this.fullName = fullname;}

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
}
