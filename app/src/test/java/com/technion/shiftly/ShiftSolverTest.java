package com.technion.shiftly;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ShiftSolverTest {
    private ArrayList<ArrayList<HashMap<String, Boolean>>> makeSched() {
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = new ArrayList<>();
        for (int k=0 ; k<7 ; k++) {
            ArrayList<HashMap<String, Boolean>> day1 = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                HashMap<String, Boolean> shift1 = new HashMap<>();
                for (int i = 0; i < 5; i++) {
                    shift1.put(Integer.toString(i), true);
                }
                day1.add(shift1);
            }
            options.add(day1);
        }
        return options;
    }
    @Test
    public void sanityTest() {
        Group group = new Group("admin", "security", 1L);
        HashMap<String, Boolean> members = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            members.put(Integer.toString(i), true);
        }
        group.setMembers(members);

        for (int i = 0; i < 5; i++) {
            assert (members.keySet().contains(Integer.toString(i)));
        }
        group.setMember_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeSched();

        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());

        System.out.println("----------------------Sanity Test----------------------");
        System.out.println("Solver result:");
        System.out.println(solver.toString());
        System.out.println("Group sched result:");
        System.out.println(group.getSchedule());
    }
    @Test
    public void constrainOnOneWorkerTest() {
        Group group = new Group("admin", "security", 1L);
        HashMap<String, Boolean> members = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            members.put(Integer.toString(i), true);
        }
        group.setMembers(members);

        for (int i = 0; i < 5; i++) {
            assert (members.keySet().contains(Integer.toString(i)));
        }
        group.setMember_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeSched();
        // Removing worker #0 from days: 1,3,5,7
        for (int k=0 ; k<7 ; k+=2) {
            for (int j = 0; j < 3; j++) {
                assert (options.get(k).get(j).remove("0"));
            }
        }
        System.out.println(options.toString());

        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());

        System.out.println("----------------------Constraint on one worker Test----------------------");
        System.out.println("Solver result:");
        System.out.println(solver.toString());
        System.out.println("Group sched result:");
        System.out.println(group.getSchedule());
    }
}