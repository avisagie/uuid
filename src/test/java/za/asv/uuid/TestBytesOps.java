package za.asv.uuid;

import org.junit.Assert;
import org.junit.Test;

import za.asv.uuid.UUIDUtil;

public class TestBytesOps {
    @Test
    public void testByteOps() {
        final long x = 0xEDF01234;
        final long y = 0x1D3C5B7A;

        byte[] bytes = UUIDUtil.toBytes(x, y);
        final long c = UUIDUtil.msbFromBytes(bytes);
        final long t = UUIDUtil.lsbFromBytes(bytes);

        Assert.assertEquals(x, c);
        Assert.assertEquals(y, t);
    }
}
