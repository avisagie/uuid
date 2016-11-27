package com.github.avisagie.uuid;

public class BenchmarkGenerator {

    public static final int NUM = 20000000;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUIDUtil.random();
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("random: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");

        start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUIDUtil.epoch();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("epoch: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");

        start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUIDUtil.uniquer();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("uniquer: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");

        start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            java.util.UUID.randomUUID();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("java.util.UUID.randomUUID: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");
    }
}
