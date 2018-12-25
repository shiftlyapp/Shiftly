package com.technion.shiftly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ShiftSolver {
    private ArrayList<ArrayList<HashMap<String, Boolean>>> schedule;
    private HashMap<String, Boolean> members;
    private long numOfMembers;
//    private ArrayList<ArrayList<HashMap<String, Boolean>>> sorted;
    private HashMap<String, Integer> numOfShiftForEmployee;

    private Comparator<HashMap<String, Boolean>> compareBySize = new Comparator<HashMap<String, Boolean>>() {
        @Override
        public int compare(HashMap<String, Boolean> list1, HashMap<String, Boolean> list2) {
            return list1.size() - list2.size();
        }
    };

    public ShiftSolver(Group group) {
        schedule = new ArrayList<>(group.getOptions());

        members = new HashMap<>(group.getMembers());

        numOfMembers = group.getMember_count();

        numOfShiftForEmployee = new HashMap<>();
        updateNumOfShiftForEmployee();
//        for(String member: group.getMembers().keySet()) {
//            numOfShiftForEmployee.put(member, 0);
//        }
//        this.sorted = schedule;
        return;
    }

//    // Sorting method
//    public void sortBySize(ArrayList<ArrayList<HashMap<String, Boolean>>> toSort) {
//        Collections.sort(toSort, compareBySize);
//    }

    private void updateNumOfShiftForEmployee() {
        for(String member: members.keySet()) {
            numOfShiftForEmployee.put(member, 0);
        }
        int numOfShifts;
        String memberId;
        for (int i = 0; i < schedule.size(); ++i) {
            for (int j = 0; j < schedule.get(i).size(); ++j) {
                if (schedule.get(i).get(j).size() == 1) {
                    memberId = schedule.get(i).get(j).keySet().toArray()[0].toString();
                    numOfShifts = numOfShiftForEmployee.get(memberId);
                    numOfShiftForEmployee.put(memberId, numOfShifts+1);
                }
            }
        }
    }
    // Minimizes the search domain by keeping necessary scheduling
    public ArrayList<ArrayList<HashMap<String, Boolean>>> minimizeSched() {
//        System.out.println(schedule);
        ArrayList<ArrayList<HashMap<String, Boolean>>> minimized = new ArrayList<>(schedule);
        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;
            updateNumOfShiftForEmployee();
            for (int i = 0; i < minimized.size(); ++i) {
                for (int j = 0; j < minimized.get(i).size(); ++j) {
                    if (minimized.get(i).get(j).size() == 0) {
                        // "0" means no valid schedule is available
                        return null;
                    }
                    if (minimized.get(i).get(j).size() == 1) {
                        Map.Entry<String, Boolean> memberInPlace = (Map.Entry<String, Boolean>) minimized.get(i).get(j).entrySet().toArray()[0];
                        // "1" means that the group member in this particular shift
                        // must be assigned to it. Therefore we can remove him from
                        // the previous and next shifts.

                        if (j == 0 && i > 0) { // first shift of the day, excluding first day of the week
                            if (minimized.get(i - 1).get(minimized.get(i - 1).size() - 1).containsKey(memberInPlace.getKey())) {
                                hasChanged = true;
                            }
                            minimized.get(i - 1).get(minimized.get(i - 1).size() - 1).remove(memberInPlace.getKey());
                        }
                        if (j == 0 && i == 0) { // first shift of the day, first day of the week
                            //Do nothing
                        }
                        if (j > 0) { // shift is in the middle of the day or later, remove the employee from previous shift
                            if (minimized.get(i).get(j - 1).containsKey(memberInPlace.getKey())) {
                                hasChanged = true;
                            }
                            minimized.get(i).get(j - 1).remove(memberInPlace.getKey());
                        }

                        if (j == minimized.get(i).size() - 1 && i < minimized.size() - 1) { // last shift of the day, excluding last day of the week
                            if (minimized.get(i + 1).get(0).containsKey(memberInPlace.getKey())) {
                                hasChanged = true;
                            }
                            minimized.get(i + 1).get(0).remove(memberInPlace.getKey());
                        }
                        if (j == minimized.get(i).size() - 1 && i == minimized.size() - 1) { // last shift of the day, last day of the week
                            //Do nothing
                        }
                        if (j < minimized.get(i).size() - 1) { // shift is in the middle of the day or sooner, remove the employee from next shift
                            if (minimized.get(i).get(j + 1).containsKey(memberInPlace.getKey())) {
                                hasChanged = true;
                            }
                            minimized.get(i).get(j + 1).remove(memberInPlace.getKey());
                        }
                    }
                }
            }
        }
        return minimized;
    }
    // TODO: add a chooser w.r.t numOfShiftForEmployee
    // TODO: Implement solver algorithm
    private int[] findMinimalUnassignedShift() {
        int[] indices = {0,0,0};
        Map<String, Boolean> firstMinimal = schedule.get(indices[0]).get(indices[1]);
        indices[2] = (int)numOfMembers+1;
        for (int i = 0; i < schedule.size(); ++i) {
            for (int j = 0; j < schedule.get(i).size(); ++j) {
                if (schedule.get(i).get(j).size() < indices[2] && schedule.get(i).get(j).size() > 1) {
                    firstMinimal = schedule.get(i).get(j);
                    indices[0] = i;
                    indices[1] = j;
                    indices[2] = schedule.get(i).get(j).size();
                }
                if (schedule.get(i).get(j).size() == 0) {
                    // "0" means no valid schedule is available
                    indices[0] = -1;
                    indices[1] = -1;
                    indices[2] = 0;
                    return indices;
                }
            }
        }
        return indices;
    }

    private Map<String, Boolean> findWorkersToRemove(Map<String, Boolean> workersInshift) {
        Map<String, Boolean> toRemove = new HashMap<>(workersInshift);
        // Removes the first worker with the minimal amount of shifts
        Map.Entry<String, Integer> minWorker = null;
        for (Map.Entry<String, Integer> worker : numOfShiftForEmployee.entrySet()) {
            if (minWorker == null || worker.getValue() < minWorker.getValue()) {
                minWorker = worker;
            }
        }
        toRemove.remove(minWorker.getKey());
        return toRemove;
    }

    public ArrayList<ArrayList<HashMap<String, Boolean>>> solve() {

        if (minimizeSched() == null) {
            System.out.println("0");
            return null;
        }
        // If there is no unassigned shift, we are done
        if (findMinimalUnassignedShift()[2] == (int)numOfMembers+1) {
            System.out.println("1");
            return schedule;
        }
        // make tentative assignment
        int[] location = findMinimalUnassignedShift();
        Map<String, Boolean> shift = schedule.get(location[0]).get(location[1]);
        //TODO: leave only one worker with the least amount of shifts
//        for (String m : members.keySet()) {
//            if (schedule.get(location[0]).get(location[1]).size() > 1) {
//                schedule.get(location[0]).get(location[1]).remove(m);
//            }
//        }

        // Remove the excessive workers from the shift
        for (Map.Entry<String, Boolean> toRemove : findWorkersToRemove(shift).entrySet()) {
            schedule.get(location[0]).get(location[1]).remove(toRemove.getKey());
        }
        // If successful return the schedule
        if(solve() != null) {
            System.out.println("2");
            return schedule;
        } else { // Failure, try another route
            location = findMinimalUnassignedShift(); // TODO: check for infinite loops
        }
        System.out.println("3");
        return null; // this triggers backtracking
    }

    @Override
    public String toString() {
        String sched = "";
        int i = 1;
        for (ArrayList<HashMap<String, Boolean>> day : schedule) {
            sched += ("day" + Integer.toString(i++) + ": " + day + "\n") ;
        }
        return "----ShiftSolver----\n" +
                "schedule:\n" + sched;
    }
}
