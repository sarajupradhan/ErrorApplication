package com.test.error;

import java.util.ArrayList;
import java.util.List;

public class MemoryEater {
    private static final int ARRAY_SIZE = Integer.MAX_VALUE / 100; // Adjust size according to needs
    public static void simulateOOM() {
        try {
            // Continuously allocate large arrays to fill up memory
            List<int[]> memoryConsumingList = new ArrayList<>();
            while (true) {
                // Attempt to add a new large array to the list
                memoryConsumingList.add(new int[ARRAY_SIZE]);
            }
        } catch (OutOfMemoryError e) {
            // Handle the OOM error
            System.out.println("OutOfMemoryError caught!");
            e.printStackTrace();
        }
    }

}
