package com.technion.shiftly.utility;

public class MethodsForTests {

    public static void wait_to_load() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}