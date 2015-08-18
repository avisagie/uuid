package asv.uuid;

import org.junit.Assert;

import java.text.ParseException;
import java.util.UUID;

public class BenchmarkStringOps {

    public static final int NUM = 2000000;

    public static void main(String[] args) throws ParseException {
        long start = System.currentTimeMillis();
        for (int ii = 0; ii < NUM; ii++) {
            final UUID x = UUIDUtil.randomUUID();
            final String s = x.toString();
            final UUID y = UUID.fromString(s);
            Assert.assertEquals(x, y);
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(NUM + " took " + elapsed + "ms @ " + (long) ((NUM * 1000.0) / elapsed) + "/s");
    }
}
