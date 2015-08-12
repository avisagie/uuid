package asv.uuid;

/**
 * Created by albert on 2015/06/07.
 */
public class Benchmark {

    public static final int NUM = 10000000;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUID.random();
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");

        start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            UUID.epoch();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println(NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");
    }
}
