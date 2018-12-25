package com.technion.shiftly;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String firstname;
    private String lastname;
    private String email;
    private Long groups_count;
    private Map<String,Boolean> groups;

    public User() {
    }

    public User(String firstname_p, String lastname_p, String email_p) {
        this.firstname = firstname_p;
        this.lastname = lastname_p;
        this.email = email_p;
        this.groups_count = 0L;
        this.groups = new HashMap<String,Boolean>() {{
            put(" ", false);
        }};
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getGroups_count() {
        return groups_count;
    }

    public void setGroups_count(Long groups_count) {
        this.groups_count = groups_count;
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }
}