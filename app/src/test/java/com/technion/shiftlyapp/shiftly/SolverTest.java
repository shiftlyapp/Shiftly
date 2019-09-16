package com.technion.shiftlyapp.shiftly;

import com.technion.shiftlyapp.shiftly.algorithm.ShiftSchedulingSolver;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Example unit test for the solver algorithm.
 *
 */
public class SolverTest {

    @Test
    public void sanityTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();

        options.put("1", "1");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);

        System.out.println(solver.getFinal_schedule());

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void noSolveTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "10100011110");
        options.put("2", "00001011110");
        options.put("3", "01100111110");
        options.put("4", "00011011110");
        options.put("5", "00001111110");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);

        List<String> schedule = solver.getFinal_schedule();
        System.out.println("No solution could be found with these constraints");

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void noSolveComplexTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "10100001111");
        options.put("2", "00001101111");
        options.put("3", "01100111111");
        options.put("4", "00010001110");
        options.put("5", "00000001110");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);

        System.out.println("No solution could be found with these constraints");

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void constraintsTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "100001000010000100001");
        options.put("2", "010000100001000010000");
        options.put("3", "001000010000100001000");
        options.put("4", "000100001000010000100");
        options.put("5", "000010000100001000010");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);
        if (scheduling_result) {
            System.out.println(solver.getFinal_schedule());
        } else {
            System.out.print("No solution could be found with these constraints");
        }

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void redundantConstraintsTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "110001100011000110001");
        options.put("2", "011000110001100011000");
        options.put("3", "001100011000110001100");
        options.put("4", "000110001100011000110");
        options.put("5", "000010000110001100011");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);
        System.out.println(solver.getFinal_schedule());

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void freeConstraintsTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "110011100111001110011");
        options.put("2", "111001110011100111000");
        options.put("3", "011100111001110011100");
        options.put("4", "001110011100111001110");
        options.put("5", "000111001110011100111");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);
        System.out.println(solver.getFinal_schedule());

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void multipleWorkersSanityTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "111111111111111111111");
        options.put("2", "111111111111111111111");
        options.put("3", "111111111111111111111");
        options.put("4", "111111111111111111111");
        options.put("5", "111111111111111111111");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,3);

        Boolean scheduling_result = solver.solve();

        if (scheduling_result) {
            System.out.println(solver.getFinal_schedule());
        } else {
            System.out.print("No solution could be found with these constraints");
        }
        assert (scheduling_result);

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void multipleWorkersNoSolveTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "111111111111111111000");
        options.put("2", "111111111111111111000");
        options.put("3", "111111111111111111000");
        options.put("4", "111111111111111111000");
        options.put("5", "111111111111111111000");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,3);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);
        System.out.println(solver.getFinal_schedule());

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void multipleWorkersNoSolveComplexTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "110000000011");
        options.put("2", "000011110011");
        options.put("3", "110000111111");
        options.put("4", "001100000011");
        options.put("5", "000000000011");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,2);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);
        System.out.print("No solution could be found with these constraints");

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void semiStressTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "101000111111111111111101000111111111111110");
        options.put("2", "000010111111111111111111111111111111111000");
        options.put("3", "011001111111111111111101010111100000111000");
        options.put("4", "000110111111100000000000001000000010000000");
        options.put("5", "000011111111111111111101000111111111111111");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,1);

        Boolean scheduling_result = solver.solve();
        assert (scheduling_result);
        System.out.println(solver.getFinal_schedule());

        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void multipleWorkersRedundantConstraintsTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "111111000000000111111");
        options.put("2", "000111111000000000111");
        options.put("3", "000000111111000000000");
        options.put("4", "000000000111111000000");
        options.put("5", "000000000000111111000");
        options.put("6", "111111000000000111111");
        options.put("7", "000111111000000000111");
        options.put("8", "000000111111000000000");
        options.put("9", "000000000111111000000");
        options.put("10", "000000000000111111000");
        options.put("11", "111111000000000111111");
        options.put("12", "000111111000000000111");
        options.put("13", "000000111111000000000");
        options.put("14", "000000000111111000000");
        options.put("15", "000000000000111111000");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,3);

        Boolean scheduling_result = solver.solve();
        if (scheduling_result) {
            System.out.println(solver.getFinal_schedule());
        } else {
            System.out.print("No solution could be found with these constraints");
        }
        assert (scheduling_result);


        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }

    @Test
    public void multipleWorkersRedundantConstraintsNoSolveTest() {
        long startTime = System.nanoTime();
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("1", "111111000000000111111");
        options.put("2", "000111111000000000111");
        options.put("3", "000000111111000000000");
        options.put("4", "000000000111111000000");
        options.put("5", "000000000000111000000");

        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(options,3);

        Boolean scheduling_result = solver.solve();
        if (scheduling_result) {
            System.out.println(solver.getFinal_schedule());
        } else {
            System.out.print("No solution could be found with these constraints");
        }
        assert (scheduling_result);


        long endTime = System.nanoTime();

        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds  : " + timeElapsed);
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 + "\n");
    }
}