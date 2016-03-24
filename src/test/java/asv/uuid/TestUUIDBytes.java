package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.UUID;

public class TestUUIDBytes {
    @Test
    public void testStringOps() throws ParseException {
        for (int ii=0; ii<100; ii++) {
            final UUID uuid = UUIDUtil.random();
            final byte[] bytes = UUIDUtil.toBytes(uuid);
            final UUID uuid2 = UUIDUtil.fromBytes(bytes);
            Assert.assertEquals(uuid, uuid2);
        }
    }
}
