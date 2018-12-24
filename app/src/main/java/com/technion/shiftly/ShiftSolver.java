package com.technion.shiftly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ShiftSolver {
    private ArrayList<ArrayList<HashMap<String, Boolean>>> schedule;
    private long numOfMembers;
//    private ArrayList<ArrayList<HashMap<String, Boolean>>> sorted;
//    private HashMap<String, Integer> numOfShiftForEmployee;

    private Comparator<HashMap<String, Boolean>> compareBySize = new Comparator<HashMap<String, Boolean>>() {
        @Override
        public int compare(HashMap<String, Boolean> list1, HashMap<String, Boolean> list2) {
            return list1.size() - list2.size();
        }
    };
    // TODO: Set the schedule
    public ShiftSolver(Group group) {
        schedule = new ArrayList<>(group.getOptions());
        numOfMembers = group.getMember_count();
//        numOfShiftForEmployee = new HashMap<>();
//        for(String member: group.getMembers().keySet()) {
//            numOfShiftForEmployee.put(member, 0);
//        }
//        this.sorted = schedule;
    }

//    // Sorting method
//    public void sortBySize(ArrayList<ArrayList<HashMap<String, Boolean>>> toSort) {
//        Collections.sort(toSort, compareBySize);
//    }
    //TODO: Edit numOfShiftForEmployee
    // Minimizes the search domain by keeping necessary scheduling
    public ArrayList<ArrayList<HashMap<String, Boolean>>> minimizeSched() {
        ArrayList<ArrayList<HashMap<String, Boolean>>> minimized = new ArrayList<>(schedule);
        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;
//            for(String member: numOfShiftForEmployee.keySet()) {
//                numOfShiftForEmployee.put(member, 0);
//            }
            for (int i = 0; i < minimized.size(); ++i) {
                for (int j = 0; j < minimized.get(i).size(); ++j) {
                    if (minimized.get(i).get(j).size() == 0) {
                        // "0" means no valid schedule is available
                        return null;
                    }
                    if (minimized.get(i).get(j).size() == 1) {
                        // "1" means that the group member in this particular shift
                        // must be assigned to it. Therefore we can remove him from
                        // the previous and next shifts.

                        if (j == 0 && i > 0) { // first shift of the day, excluding first day of the week
                            minimized.get(i - 1).get(minimized.get(i - 1).size() - 1).remove(minimized.get(i).get(j).keySet().toString());
                            hasChanged = true;
                        }
                        if (j == 0 && i == 0) { // first shift of the day, first day of the week
                            //Do nothing
                        }
                        if (j > 0) { // shift is in the middle of the day or later, remove the employee from previous shift
                            minimized.get(i).get(j - 1).remove(minimized.get(i).get(j).keySet().toString());
                            hasChanged = true;
                        }

                        if (j == minimized.get(i).size() - 1 && i < minimized.size() - 1) { // last shift of the day, excluding last day of the week
                            minimized.get(i + 1).get(0).remove(minimized.get(i).get(j).keySet().toString());
                            hasChanged = true;
                        }
                        if (j == minimized.get(i).size() - 1 && i == minimized.size() - 1) { // last shift of the day, last day of the week
                            //Do nothing
                        }
                        if (j < minimized.get(i).size() - 1) { // shift is in the middle of the day or sooner, remove the employee from next shift
                            minimized.get(i).get(j + 1).remove(minimized.get(i).get(j).keySet().toString());
                            hasChanged = true;
                        }
                    }
                }
            }
        }
        return minimized;
    }
    // TODO: add a chooser w.r.t numOfShiftForEmployee
    // TODO: Implement solver algorithm
    public int[] findMinimalUnassignedShift() {
        int[] indices = {0,0,0};
        Map<String, Boolean> firstMinimal = schedule.get(indices[0]).get(indices[1]);
        indices[2] = (int)numOfMembers;
        for (int i = 0; i < schedule.size(); ++i) {
            for (int j = 0; j < schedule.get(i).size(); ++j) {
                if (schedule.get(i).get(j).size() < firstMinimal.size() && schedule.get(i).get(j).size() > 1) {
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

    public ArrayList<ArrayList<HashMap<String, Boolean>>> solve() {

        if (minimizeSched() == null) {
            return null;
        }
        // If there is no unassigned shift, we are done
        if (findMinimalUnassignedShift()[2] == (int)numOfMembers) {
            return schedule;
        }
        // make tentative assignment
        int[] location = findMinimalUnassignedShift();
        Map<String, Boolean> shift = schedule.get(location[0]).get(location[1]);
        String memberToRemove = shift.keySet().iterator().toString();
        schedule.get(location[0]).get(location[1]).remove(memberToRemove);
        // If successful return the schedule
        if(solve() != null) {
            return schedule;
        } else { // Failure, try another route
            schedule.get(location[0]).get(location[1]).put(memberToRemove, true);
            location = findMinimalUnassignedShift(); // TODO: check for infinite loops
        }

        return null; // this triggers backtracking
    }
}
