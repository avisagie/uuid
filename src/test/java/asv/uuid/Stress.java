package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

/**
 * Created by albert on 2015/08/12.
 */
public class Stress {
    private static final int NUM = 20000000;

    @Test
    public void random() {
        HashSet<UUID> all = new HashSet<>(NUM, 0.75f);
        for (int ii=0; ii<NUM; ii++) {
            all.add(UUID.random());
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
