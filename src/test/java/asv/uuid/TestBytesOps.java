package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

public class TestBytesOps {
    @Test
    public void testByteOps() {
        final long x = 0xEDF01234;
        final long y = 0x1D3C5B7A;

        byte[] bytes = UUID.toBytes(x, y);
        final long c = UUIDUtil.msbFromBytes(bytes);
        final long t = UUIDUtil.lsbFromBytes(bytes);

        Assert.assertEquals(x, c);
        Assert.assertEquals(y, t);
    }
}
