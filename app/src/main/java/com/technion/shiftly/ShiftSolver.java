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

    private ArrayList<ArrayList<HashMap<String, Boolean>>> deepCopyOptions(ArrayList<ArrayList<HashMap<String, Boolean>>> opt) {
        ArrayList<ArrayList<HashMap<String, Boolean>>> sched = new ArrayList<>();
        for (int k=0 ; k < opt.size() ; k++) {
            ArrayList<HashMap<String, Boolean>> day = new ArrayList<>();
            for (int j = 0; j < opt.get(k).size() ; j++) {
                HashMap<String, Boolean> shift = new HashMap<>();
                for (int i = 0; i < opt.get(k).get(j).size() ; i++) {
                    shift.put(Integer.toString(i), true);
                }
                day.add(shift);
            }
            sched.add(day);
        }
        return sched;
    }

    public ShiftSolver(Group group) {
        schedule = deepCopyOptions(group.getOptions());

        members = new HashMap<>(group.getMembers());

        numOfMembers = group.getMember_count();

        numOfShiftForEmployee = new HashMap<>();
        updateNumOfShiftForEmployee();
    }

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
                    if ((minimized.get(i).get(j)).size() == 0) {
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

    private int[] findNextMinimalUnassignedShift(int[] indices) {
        Map<String, Boolean> firstMinimal = schedule.get(indices[0]).get(indices[1]);
        indices[2] = (int)numOfMembers+1;
        for (int i = indices[0]; i < schedule.size(); ++i) {
            for (int j = indices[1]; j < schedule.get(i).size(); ++j) {
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
            if ((toRemove.containsKey(worker.getKey()) && minWorker == null) || (toRemove.containsKey(worker.getKey()) && (worker.getValue() < minWorker.getValue()))) {
                minWorker = worker;
            }
        }
        toRemove.remove(minWorker.getKey());
        return toRemove;
    }

    public ArrayList<ArrayList<HashMap<String, Boolean>>> solve() {

        if (minimizeSched() == null) {
//            System.out.println("0");
            return null;
        }
        // If there is no unassigned shift, we are done
        int[] indices = {0,0,0};
        if (findNextMinimalUnassignedShift(indices)[2] == (int)numOfMembers+1) {
//            System.out.println("1");
            return schedule;
        }
        // make tentative assignment
        int[] location = {0,0,0};
        while (location[0] != -1 || location[1] != -1) {

            location = findNextMinimalUnassignedShift(location);
            Map<String, Boolean> shift = schedule.get(location[0]).get(location[1]);

            // Remove the excessive workers from the shift
            for (Map.Entry<String, Boolean> toRemove : findWorkersToRemove(shift).entrySet()) {
                schedule.get(location[0]).get(location[1]).remove(toRemove.getKey());
            }
            // If successful return the schedule
            if(solve() != null) {
//            System.out.println("2");
                return schedule;
            } else { // Failure, try another route
                return null;
//                location = findNextMinimalUnassignedShift(location);
//                return null;
            }

        }


//        System.out.println("3");
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
