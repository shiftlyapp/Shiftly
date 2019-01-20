package com.technion.shiftly.dataTypes;

import java.util.ArrayList;
import java.util.Map;

// A class that represents a group in the app.
// This group syncs with the firebase database

public class Group {

    private String admin;
    private String group_name;
    private Map<String, Boolean> members;
    private Long members_count;
    private Long days_num;
    private Long shifts_per_day;
    private Long employees_per_shift;
    private String group_icon_url;
    private String starting_time;
    private Long shift_len;
    private Map<String, String> options; // Keys = UUID's, Values = binary vectors
    private ArrayList<String> schedule; // UUID's: at the i'th position works shift i

    public Group() {
    }

    public ArrayList<String> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<String> schedule) {
        this.schedule = schedule;
    }

    public Group(String admin, String group_name, Long members_count,
                 Long days_num, Long shifts_per_day, Long employees_per_shift,
                 String starting_time, Long shift_len) {
                 Long days_num, Long shifts_per_day, Long employees_per_shift, String group_icon_url) {
        this.admin = admin;
        this.group_name = group_name;
        this.members_count = members_count;
        this.days_num = days_num;
        this.shifts_per_day = shifts_per_day;
        this.employees_per_shift = employees_per_shift;
        this.starting_time = starting_time;
        this.shift_len = shift_len;
        this.group_icon_url = group_icon_url;
    }

    public String getAdmin() {
        return admin;
    }

    public String getGroup_name() {
        return group_name;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public Long getMembers_count() {
        return members_count;
    }

    public String getGroup_icon_url() {
        return group_icon_url;
    }

    public void setGroup_icon_url(String group_icon_url) {
        this.group_icon_url = group_icon_url;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public void setMembers_count(Long member_count) {
        this.members_count = member_count;
    }

    public Long getDays_num() {
        return days_num;
    }

    public Long getShifts_per_day() {
        return shifts_per_day;
    }

    public Long getEmployees_per_shift() {
        return employees_per_shift;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public void setDays_num(Long days_num) {
        this.days_num = days_num;
    }

    public void setShifts_per_day(Long shifts_per_day) {
        this.shifts_per_day = shifts_per_day;
    }

    public void setEmployees_per_shift(Long employees_per_shift) {
        this.employees_per_shift = employees_per_shift;
    }
}