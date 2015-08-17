package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

public class TestUUIDString {
    @Test
    public void testStringOps() throws ParseException {
        check("00000000-0000-0000-0000-000000000000");
        check("{aadb0156-a6e7-4a98-85c0-95258d0830a1}");
    }

    private void check(String s) throws ParseException {
        Assert.assertEquals(s.replace("{", "").replace("}", ""), UUID.fromString(s).toString());
    }

    @Test
    public void testStringOpsRandom() throws ParseException {
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
