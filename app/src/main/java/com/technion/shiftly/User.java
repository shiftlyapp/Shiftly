package com.technion.shiftly;

public class User {

    private String firstname;
    private String lastname;
    private String email;

    public User() {
    }

    public User(String firstname_p, String lastname_p, String email_p) {
        this.firstname = firstname_p;
        this.lastname = lastname_p;
        this.email = email_p;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String fn) {
        this.firstname = fn;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String ln) {
        this.lastname = ln;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String ea) {
        this.email = ea;
    }
}