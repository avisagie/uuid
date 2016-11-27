package com.github.avisagie.uuid;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.UUID;

public class TestUUIDString {
    @Test
    public void testStringOps() throws ParseException {
        check("00000000-0000-0000-0000-000000000000");
    }

    private void check(String s) throws ParseException {
        Assert.assertEquals(s.replace("{", "").replace("}", ""), UUID.fromString(s).toString());
    }

    @Test
    public void testStringOpsRandom() throws ParseException {
        for (int ii=0; ii<100; ii++) {
            final UUID uuid = UUIDUtil.random();
            String s = uuid.toString();
            final UUID uuid2 = UUID.fromString(s);
            Assert.assertEquals(uuid, uuid2);
        }
    }
}
