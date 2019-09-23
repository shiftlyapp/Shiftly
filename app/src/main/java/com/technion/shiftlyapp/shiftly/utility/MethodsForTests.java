package com.technion.shiftlyapp.shiftly.utility;

public class MethodsForTests {

    public static void
    wait_to_load() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}