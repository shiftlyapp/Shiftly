package com.technion.shiftly;

import java.util.HashSet;

public class User {

    private String firstname;
    private String lastname;
    private String email;
    private String groups_count;
    private HashSet<String> groups;

    public User() {
    }

    public User(String firstname_p, String lastname_p, String email_p) {
        this.firstname = firstname_p;
        this.lastname = lastname_p;
        this.email = email_p;
        this.groups_count = 0;
        this.groups = new HashSet<>();
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

    public int getGroups_count() {
        return groups_count;
    }

    public HashSet<String> getGroups() {
        return groups;
    }
}