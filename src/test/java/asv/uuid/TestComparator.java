package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class TestComparator {

    /**
     * lexicographic sorting by MSB first must equals lexicographic sorting on the toString.
     */
    @Test
    public void test() {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (int ii = 0; ii < 100; ii++) {
            uuids.add(UUID.random());
        }

        ArrayList<String> strings = new ArrayList<String>();
        for (UUID x : uuids) {
            strings.add(x.toString());
        }

        Collections.sort(uuids);
        Collections.sort(strings);

        for (int ii = 0; ii < 100; ii++) {
            Assert.assertEquals(uuids.get(ii).toString(), strings.get(ii).toString());
        }
    }
}
