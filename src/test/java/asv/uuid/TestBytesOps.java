package asv.uuid;

import org.junit.Assert;
import org.junit.Test;

public class TestBytesOps {
    @Test
    public void testByteOps() {
        final long x = 0xEDF01234;
        final long y = 0x1D3C5B7A;

        byte[] bytes = UUID.toBytes(x, y);
        final long c = UUID.msbFromBytes(bytes);
        final long t = UUID.lsbFromBytes(bytes);

        Assert.assertEquals(x, c);
        Assert.assertEquals(y, t);
    }
}
