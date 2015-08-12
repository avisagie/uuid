package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by albert on 2015/06/07.
 */
public class TestUUIDBytes {
    @Test
    public void testStringOps() throws ParseException {
        for (int ii=0; ii<100; ii++) {
            final UUID uuid = UUID.random();
            final byte[] bytes = uuid.toBytes();
            final UUID uuid2 = UUID.fromBytes(bytes);
            Assert.assertEquals(uuid, uuid2);
        }
    }
}
