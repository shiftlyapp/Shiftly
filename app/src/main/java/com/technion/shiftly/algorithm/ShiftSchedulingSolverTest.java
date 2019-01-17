package com.technion.shiftly.algorithm;

import java.util.LinkedHashMap;
import java.util.List;

public class ShiftSchedulingSolverTest {

    public static void main(String[] args) {

        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("Avi", "11110");
        options.put("David", "11110");
        options.put("Arik", "11110");
        options.put("BBB", "11110");
        options.put("CCC", "11111");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options);

        Boolean scheduling_result = solver.solve();
        if (scheduling_result) {
            List<String> schedule = solver.getFinal_schedule();

            for (int i = 0; i < schedule.size(); i++) {
                System.out.print(schedule.get(i) + "\n");
            }
        } else {
            System.out.print("No solution could be found with these constraints");
        }

    }


}
