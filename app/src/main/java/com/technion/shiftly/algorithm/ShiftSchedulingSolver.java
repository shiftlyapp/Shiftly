package com.technion.shiftly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ShiftSchedulingSolver {

    private LinkedHashMap<String, String> options;
    private List<String> final_schedule;
    private int total_shifts_num;

    public ShiftSchedulingSolver(LinkedHashMap<String, String> options) {
        this.options = options;
        this.final_schedule = new ArrayList<>();
        this.total_shifts_num = options.entrySet().iterator().next().getValue().length();
    }

    public List<String> getFinal_schedule() {
        return final_schedule;
    }

    // If returns true, then the final_schedule can be accessed with the method getFinalSchedule
    // on the ShiftSchedulingSolver object
    Boolean solve() {
        return solve_aux(0, this.options);
    }


    Boolean solve_aux(int starting_sched_from_shift, LinkedHashMap<String, String> options_aux) {

        // Base case of the recursion: if the starting_sched_from_shift == total_shift_num - we are done
        if (starting_sched_from_shift == this.total_shifts_num) return true;

        for (LinkedHashMap.Entry<String, String> entry : options_aux.entrySet()) {

            Boolean employee_can_work_this_shift = (entry.getValue().charAt(starting_sched_from_shift) == '1');
            Boolean employee_wasnt_scheduled_for_previous_shift = (starting_sched_from_shift == 0) ||
                    !(this.final_schedule.get(starting_sched_from_shift - 1).equals(entry.getKey()));

            // If the employee can work this shift and she wasn't scheduled for the previous one
            if (employee_can_work_this_shift && employee_wasnt_scheduled_for_previous_shift) {

                // Schedule her to work this shift
                this.final_schedule.add(entry.getKey());

                LinkedHashMap<String, String> shuffled_options = shuffle_options(options_aux);

                // --------- THE RECURSIVE CALL -----------
                Boolean result = solve_aux(starting_sched_from_shift + 1, shuffled_options);

                // A viable solution has been found
                if (result) return true; // The solution is already written in the final_schedule field
            }
        }
        return false;

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


}
