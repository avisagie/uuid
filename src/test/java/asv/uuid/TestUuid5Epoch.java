package asv.uuid;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class TestUuid5Epoch {
	@Test
	public void test() throws UnsupportedEncodingException {
		final UUID u = UUIDUtil.uuid5epoch("hello world".getBytes("utf8"), 1455170823868L);
		final UUID w = UUIDUtil.uuid5epoch("hello world".getBytes("utf8"), 1455170823868L + 1000L);
		Assert.assertEquals(u.toString().substring(8), w.toString().substring(8));		
		Assert.assertTrue(u.toString().startsWith("56bc2507"));
		Assert.assertTrue(w.toString().startsWith("56bc2508"));
	}
}
