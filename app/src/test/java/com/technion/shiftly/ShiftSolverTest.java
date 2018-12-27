package com.technion.shiftly;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ShiftSolverTest {
    private ArrayList<ArrayList<HashMap<String, Boolean>>> makeFullSched() {
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
    //TODO: Add test case of unsolvable schedule

    @Test
    public void unsolvableScheduleTest() {
        Group group = new Group("admin", "security", 1L);
        HashMap<String, Boolean> members = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            members.put(Integer.toString(i), true);
        }
        group.setMembers(members);

        for (int i = 0; i < 5; i++) {
            assert (members.keySet().contains(Integer.toString(i)));
        }
        group.setMembers_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeFullSched();
        for (int k=0 ; k<5 ; k++) {
            assert (options.get(0).get(0).remove(Integer.toString(k)));
        }

        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());
        assert(group.getSchedule() == null);

        System.out.println("----------------------unsolvable Schedule Test----------------------");
        System.out.println("Group sched result:");
        System.out.println(group.toString());
    }

    @Test
    public void unsolvableSchedule2Test() {
        Group group = new Group("admin", "security", 1L);
        HashMap<String, Boolean> members = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            members.put(Integer.toString(i), true);
        }
        group.setMembers(members);

        for (int i = 0; i < 5; i++) {
            assert (members.keySet().contains(Integer.toString(i)));
        }
        group.setMembers_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeFullSched();
        for (int k=0 ; k<5 ; k++) {
            assert (options.get(6).get(2).remove(Integer.toString(k)));
        }

        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());
        assert(group.getSchedule() == null);

        System.out.println("----------------------unsolvable Schedule Test 2----------------------");
        System.out.println("Group sched result:");
        System.out.println(group.toString());
    }

    @Test
    public void unsolvableSchedule3Test() {
        Group group = new Group("admin", "security", 1L);
        HashMap<String, Boolean> members = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            members.put(Integer.toString(i), true);
        }
        group.setMembers(members);

        for (int i = 0; i < 5; i++) {
            assert (members.keySet().contains(Integer.toString(i)));
        }
        group.setMembers_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeFullSched();
        for (int k=0 ; k<5 ; k++) {
            assert (options.get(1).get(2).remove(Integer.toString(k)));
        }

        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());
        assert(group.getSchedule() == null);

        System.out.println("----------------------unsolvable Schedule Test 3----------------------");
        System.out.println("Group sched result:");
        System.out.println(group.toString());
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
        group.setMembers_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeFullSched();

        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());
        assert(group.getSchedule() != null);

        System.out.println("----------------------Sanity Test----------------------");
        System.out.println("Group sched result:");
        System.out.println(group.toString());
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
        group.setMembers_count(5L);
        // Setting up options
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = makeFullSched();
        // Removing worker #0 from days: 1,3,5,7
        for (int k=0 ; k<7 ; k+=2) {
            for (int j = 0; j < 3; j++) {
                assert (options.get(k).get(j).remove("0"));
            }
        }
        group.setOptions(options);

        // setting up solver
        ShiftSolver solver = new ShiftSolver(group);
        group.setSchedule(solver.solve());

        assert(group.getSchedule() != null);

        System.out.println("----------------------Constraint on one worker Test----------------------");
        System.out.println("Group sched result:");
        System.out.println(group.toString());
    }
}