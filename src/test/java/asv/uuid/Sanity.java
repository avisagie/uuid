package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.UUID;

public class Sanity {
    private static final int NUM = 10000000;

    @Test
    public void random() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUIDUtil.random());
        }

        Assert.assertEquals(NUM, all.size());
    }

    @Test
    public void epoch() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUIDUtil.epoch());
        }

        Assert.assertEquals(NUM, all.size());
    }

    @Test
    public void uniquer() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUIDUtil.uniquer());
        }

        Assert.assertEquals(NUM, all.size());
    }
}
