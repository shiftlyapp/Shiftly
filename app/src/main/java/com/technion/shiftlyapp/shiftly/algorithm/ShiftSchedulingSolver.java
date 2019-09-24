package com.technion.shiftlyapp.shiftly.algorithm;

import com.technion.shiftlyapp.shiftly.utility.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ShiftSchedulingSolver {

    private LinkedHashMap<String, String> options;
    private List<String> final_schedule;
    private int total_shifts_num;
    private int workers_in_shift;

    public ShiftSchedulingSolver(LinkedHashMap<String, String> options, int workers_in_shift) {
        this.options = options;
        this.workers_in_shift = workers_in_shift;
        this.final_schedule = new ArrayList<>();
        this.total_shifts_num = options.entrySet().iterator().next().getValue().length();
    }

    public List<String> getFinal_schedule() {
        return final_schedule;
    }

    // If returns true, then the final_schedule can be accessed with the method getFinalSchedule
    // on the ShiftSchedulingSolver object
    public Boolean solve() {
        minimize_sched(options, true);
        return solve_aux(0, this.options);
    }

    // Given an options map, removing all the redundant options
    void minimize_sched(LinkedHashMap<String, String> options, boolean changed) {
        if (!changed) {
            return;
        }
        Boolean has_changed = false;
        for (int i=0 ; i < total_shifts_num ; i += workers_in_shift) {
            int num_of_employees_can_work_this_shift = 0;
            LinkedHashMap.Entry<String, String> only_worker_available = null;
            for (LinkedHashMap.Entry<String, String> entry : options.entrySet()) {
                if (entry.getValue().charAt(i) == '1') {
                    num_of_employees_can_work_this_shift++;
                    only_worker_available = entry;
                }
            }

            if (num_of_employees_can_work_this_shift == 1) {
                if (i>0) {
                    if (only_worker_available.getValue().charAt(i-workers_in_shift) == '1') {
                        has_changed = true;
                    }
                    // removing the shift before
                    String replace = "";
                    for(int j = 0; j < workers_in_shift ; j++) {
                        replace += '0';
                    }
                    String updated_options = only_worker_available.getValue().substring(0,i-workers_in_shift)
                            + replace +only_worker_available.getValue().substring(i);
                    only_worker_available.setValue(updated_options);
                }
                if (i<total_shifts_num-workers_in_shift) {
                    if (only_worker_available.getValue().charAt(i+workers_in_shift) == '1') {
                        has_changed = true;
                    }
                    // removing the shift after
                    String replace = "";
                    for(int j = 0; j < workers_in_shift ; j++) {
                        replace += '0';
                    }
                    String updated_options = only_worker_available.getValue().substring(0,i+workers_in_shift)
                            + replace;
                    if (i < total_shifts_num-2*workers_in_shift)
                        updated_options += only_worker_available.getValue().substring(i+2*workers_in_shift);
                    only_worker_available.setValue(updated_options);
                }
            }
        }
        minimize_sched(options, has_changed);
    }

    Boolean solve_aux(int starting_sched_from_shift, LinkedHashMap<String, String> options_aux) {

        LinkedHashMap<String, String> shuffled_options = shuffle_options(options_aux);

        // Base case of the recursion: if the starting_sched_from_shift == total_shift_num - we are done
        if (final_schedule.size() == total_shifts_num) return true;

        for (LinkedHashMap.Entry<String, String> entry : shuffled_options.entrySet()) {

            Boolean employee_can_work_this_shift = (entry.getValue().charAt(starting_sched_from_shift) == '1');
            Boolean is_first_shift = (starting_sched_from_shift/workers_in_shift == 0);

            int employees_already_assigned_for_shift = starting_sched_from_shift % workers_in_shift;

            // Validate that the employee was not assigned already to the previous shift
            Boolean employee_wasnt_scheduled_for_previous_shift = true;
            if (!is_first_shift) {
                for (int i = 0 ; i < workers_in_shift ; i++) {
                    employee_wasnt_scheduled_for_previous_shift = employee_wasnt_scheduled_for_previous_shift &&
                            !(this.final_schedule.get(starting_sched_from_shift - employees_already_assigned_for_shift - i - 1).equals(entry.getKey()));
                }
            }

            employee_wasnt_scheduled_for_previous_shift = employee_wasnt_scheduled_for_previous_shift || is_first_shift;

            // Validate that the employee was not assigned already to the current shift
            Boolean employee_is_not_scheduled_for_current_shift = true;
            for (int i = 0 ; i < employees_already_assigned_for_shift ; i++) {
                employee_is_not_scheduled_for_current_shift = employee_is_not_scheduled_for_current_shift &&
                        !(this.final_schedule.get(starting_sched_from_shift - i - 1).equals(entry.getKey()));
            }

            // If the employee can work this shift and she wasn't scheduled for the previous one
            if (employee_can_work_this_shift && employee_wasnt_scheduled_for_previous_shift
                && employee_is_not_scheduled_for_current_shift) {

                // Schedule her to work this shift
                this.final_schedule.add(entry.getKey());


                // --------- THE RECURSIVE CALL -----------
                Boolean result = solve_aux(starting_sched_from_shift + 1, shuffled_options);

                // A viable solution has been found
                if (result) return true; // The solution is already written in the final_schedule field

            }
        }

        // In case no employee can work this shift - add "N/A" to the schedule and continue
        String NA = Constants.NA;
        this.final_schedule.add(NA);
        Boolean res = solve_aux(starting_sched_from_shift + 1, shuffled_options);
        return res;
    }

    LinkedHashMap<String, String> shuffle_options(LinkedHashMap<String, String> options_to_shuffle) {
        // --------- ADDS RANDOMIZATION -----------
        // Shuffle the options and send a new options map before performing the recursive call
        // Shuffle the options map so that the shifts are evenly distributed and each solving
        // can produce a different result
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        List<String> options_keys = new ArrayList<>(options_to_shuffle.keySet());
        Collections.shuffle(options_keys);

        List<String> options_values = new ArrayList<>();
        for (int i = 0; i < options_keys.size(); i++) {
            options_values.add(options_to_shuffle.get(options_keys.get(i)));
        }
        for (int i = 0; i < options_keys.size(); i++) {
            result.put(options_keys.get(i), options_values.get(i));
        }
        return result;
    }

    /*
    This function
     */
    @Override
    public String toString() {
        String sched = "";
        int num_of_shift = 1;
        for (String employee : final_schedule) {
            if (num_of_shift % workers_in_shift != 0) {
                sched += employee + ",";
            } else {
                sched += "\n";
                sched += "Shift #" + num_of_shift/workers_in_shift + ":\n";
                sched += employee + ",";
            }
            num_of_shift++;
        }
        return "----ShiftSolver----\n" +
                "schedule:\n" + sched;
    }
}
