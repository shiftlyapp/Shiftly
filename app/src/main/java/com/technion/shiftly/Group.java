package com.technion.shiftly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {

    private String admin;
    private String group_name;
    private Map<String, Boolean> members;
    private Long member_count;
    private ArrayList<ArrayList<HashMap<String, Boolean>>> options;

    private ArrayList<ArrayList<HashMap<String, Boolean>>> schedule;
    private List<Map<String, Boolean>> timeslots;

    public Group() {
    }

    public Group(String admin, String group_name, Long member_count) {
        this.admin = admin;
        this.group_name = group_name;
        this.member_count = member_count;
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

    public Long getMember_count() {
        return member_count;
    }

    public ArrayList<ArrayList<HashMap<String, Boolean>>> getOptions() {
        return options;
    }

    public List<Map<String, Boolean>> getTimeslots() {
        return timeslots;
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

    public void setMember_count(Long member_count) {
        this.member_count = member_count;
    }

    public void setOptions(ArrayList<ArrayList<HashMap<String, Boolean>>> options) {
        this.options = options;
    }

    public void setTimeslots(List<Map<String, Boolean>> timeslots) {
        this.timeslots = timeslots;
    }

    public ArrayList<ArrayList<HashMap<String, Boolean>>> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<ArrayList<HashMap<String, Boolean>>> schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        String sched = "";
        int i = 1;
        if (schedule != null) {
            for (ArrayList<HashMap<String, Boolean>> day : schedule) {
                sched += ("day" + Integer.toString(i++) + ": " + day + "\n") ;
            }
        } else {
            sched = "Unresolved";
        }
        String opts = "";
        int j = 1;
        if (options != null) {
            for (ArrayList<HashMap<String, Boolean>> day : options) {
                opts += ("day" + Integer.toString(j++) + ": " + day + "\n") ;
            }
        } else {
            opts = "None Exist";
        }
        String slots = "";
        int k = 1;
        if (timeslots != null) {
            for (Map<String, Boolean> shift : timeslots) {
                slots += ("shift" + Integer.toString(j++) + ": " + shift + "\n") ;
            }
        } else {
            slots = "None Exist";
        }

        return "-----Group----\n" +
                "group_name: '" + group_name + "'\n" +
                "admin: '" + admin + "'\n" +
                "members: " + members + "\n" +
                "member_count: " + member_count + "\n" +
                "\nOptions: \n" + opts + "\n" +
                "Schedule: \n" + sched + "\n" +
                "timeslots: \n" + slots + "\n";
    }
}