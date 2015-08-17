package asv.uuid;

public class BenchmarkGenerator {

    public static final int NUM = 10000000;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUID.random();
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("asv.uuid.UUID.random: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");

        start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUID.epoch();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("asv.uuid.UUID.epoch: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");

        start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            java.util.UUID.randomUUID();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("java.util.UUID.randomUUID: " + NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");
    }
}
