package com.technion.shiftly.algorithm;

import java.util.LinkedHashMap;
import java.util.List;

public class ShiftSchedulingSolverTest {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "101000111111111111111101000111111111111110");
        options.put("2", "000010111111111111111111111111111100000000");
        options.put("3", "011001111111111111111101010111100000000000");
        options.put("4", "000110111111100000000000001000000000000000");
        options.put("5", "000011111111111111111101000111111111111111");

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

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);

        System.out.println("Execution time in milliseconds : " +
                timeElapsed / 1000000);

    }


}
