package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

public class Sanity {
    private static final int NUM = 10000000;

    @Test
    public void random() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUID.random());
        }

        Assert.assertEquals(NUM, all.size());
    }

    @Test
    public void sha1() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUID.sha1());
        }

        Assert.assertEquals(NUM, all.size());
    }

    @Test
    public void epoch() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUID.epoch());
        }

        Assert.assertEquals(NUM, all.size());
    }
}
