package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by albert on 2015/06/07.
 */
public class TestUUIDString {
    @Test
    public void testStringOps() throws ParseException {
        for (int ii=0; ii<100; ii++) {
            final UUID uuid = UUID.random();
            String s = uuid.toString();
            if (ii % 2 == 0) {
                s = "{" + s + "}";
            }
            final UUID uuid2 = UUID.fromString(s);
            Assert.assertEquals(uuid, uuid2);
        }
    }
}
