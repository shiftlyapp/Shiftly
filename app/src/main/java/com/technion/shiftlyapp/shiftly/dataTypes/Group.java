package com.technion.shiftlyapp.shiftly.dataTypes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

// A class that represents a group in the app.
// This group syncs with the firebase database
public class Group implements Parcelable {

    private String admin;
    private String group_name;
    private HashMap<String, String> members;
    private Long members_count;
    private Long days_num;
    private Long shifts_per_day;
    private Long employees_per_shift;
    private String group_icon_url;
    private String starting_time;
    private Long shift_length;
    private HashMap<String, String> options; // Keys = UUID's, Values = binary vectors
    private ArrayList<String> schedule; // UUID's: at the i'th position works shift i

    public Group() {
        members_count = 0L;
        members = new HashMap<>();
        options = new HashMap<>();
        schedule = new ArrayList<>();
    }

    public Group(String admin, String group_name, Long members_count,
                 Long days_num, Long shifts_per_day, Long employees_per_shift,
                 String starting_time, Long shift_length, String group_icon_url) {
        this.admin = admin;
        this.group_name = group_name;
        this.members_count = members_count;
        this.days_num = days_num;
        this.shifts_per_day = shifts_per_day;
        this.employees_per_shift = employees_per_shift;
        this.starting_time = starting_time;
        this.shift_length = shift_length;
        this.group_icon_url = group_icon_url;
        this.options = new HashMap<>();
        this.schedule= new ArrayList<>();
    }

    // Copy C'tor
    public Group(Group g) {
        this.admin = g.admin;
        this.group_name = g.group_name;
        this.members_count = g.members_count;
        this.days_num = g.days_num;
        this.shifts_per_day = g.shifts_per_day;
        this.employees_per_shift = g.employees_per_shift;
        this.starting_time = g.starting_time;
        this.shift_length = g.shift_length;
        this.group_icon_url = g.group_icon_url;

        this.members = cloneMap(g.members);
        this.options = cloneMap(g.options);
        this.schedule = cloneList(g.schedule);
    }

    public ArrayList<String> getSchedule() {
        return schedule;
    }
    public void setSchedule(ArrayList<String> schedule) {
        this.schedule = schedule;
    }

    public String getStarting_time() {
        return starting_time;
    }
    public void setStarting_time(String starting_time) {
        this.starting_time = starting_time;
    }

    public Long getShift_length() {
        return shift_length;
    }
    public void setShift_length(Long shift_length) {
        this.shift_length = shift_length;
    }

    public String getAdmin() {
        return admin;
    }
    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGroup_name() {
        return group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }
    public void setMembers(HashMap<String, String> members) {
        this.members = members;
    }

    public Long getMembers_count() {
        return members_count;
    }
    public void setMembers_count(Long member_count) {
        this.members_count = member_count;
    }

    public String getGroup_icon_url() {
        return group_icon_url;
    }
    public void setGroup_icon_url(String group_icon_url) {
        this.group_icon_url = group_icon_url;
    }

    public HashMap<String, String> getOptions() {
        return options;
    }
    public void setOptions(HashMap<String, String> options) {
        this.options = options;
    }

    public Long getDays_num() {
        return days_num;
    }
    public void setDays_num(Long days_num) {
        this.days_num = days_num;
    }

    public Long getShifts_per_day() {
        return shifts_per_day;
    }
    public void setShifts_per_day(Long shifts_per_day) {
        this.shifts_per_day = shifts_per_day;
    }

    public Long getEmployees_per_shift() {
        return employees_per_shift;
    }
    public void setEmployees_per_shift(Long employees_per_shift) {
        this.employees_per_shift = employees_per_shift;
    }

    private ArrayList<String> cloneList(ArrayList<String> list) {
        ArrayList<String> clone = new ArrayList<String>(list.size());
        clone.addAll(list);
        return clone;
    }

    private HashMap<String, String> cloneMap(HashMap<String, String> map) {
        HashMap<String, String> clone = new HashMap<>(map.size());
        clone.putAll(map);
        return clone;
    }

    ///
    // --- Ability to pass Group into another intent ---
    ///
    protected Group(Parcel in) {
        admin = in.readString();
        group_name = in.readString();
        if (in.readByte() == 0) {
            members_count = null;
        } else {
            members_count = in.readLong();
        }
        if (in.readByte() == 0) {
            days_num = null;
        } else {
            days_num = in.readLong();
        }
        if (in.readByte() == 0) {
            shifts_per_day = null;
        } else {
            shifts_per_day = in.readLong();
        }
        if (in.readByte() == 0) {
            employees_per_shift = null;
        } else {
            employees_per_shift = in.readLong();
        }
        group_icon_url = in.readString();
        starting_time = in.readString();
        if (in.readByte() == 0) {
            shift_length = null;
        } else {
            shift_length = in.readLong();
        }
        schedule = in.createStringArrayList();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(admin);
        dest.writeString(group_name);
        if (members_count == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(members_count);
        }
        if (days_num == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(days_num);
        }
        if (shifts_per_day == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(shifts_per_day);
        }
        if (employees_per_shift == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(employees_per_shift);
        }
        dest.writeString(group_icon_url);
        dest.writeString(starting_time);
        if (shift_length == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(shift_length);
        }
        dest.writeStringList(schedule);
    }
}