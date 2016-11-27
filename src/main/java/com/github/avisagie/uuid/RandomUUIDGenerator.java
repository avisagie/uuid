package com.github.avisagie.uuid;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by albert on 2015/08/16.
 */
class RandomUUIDGenerator {
    // TODO Add IP or MAC?

    private static final AtomicLong counter;
    private static final long start;
    private static final SecretKeySpec skeySpec;
    private static final ThreadLocal<Cipher> cipher = new ThreadLocal<Cipher>() {
        @Override
        protected Cipher initialValue() {
            try {
                final Cipher ciph = Cipher.getInstance("AES/ECB/NoPadding");
                ciph.init(Cipher.ENCRYPT_MODE, skeySpec);
                return ciph;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    };

    static {
        try {
            final SecureRandom rand = new SecureRandom();
            counter = new AtomicLong(rand.nextLong());
            start = rand.nextLong();

            final byte[] keyBytes = new byte[16];
            rand.nextBytes(keyBytes);
            skeySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static java.util.UUID nextJava() {
        final byte[] ret = nextBytes();
        final long lsb = UUIDUtil.lsbFromBytes(ret);
        final long msb = UUIDUtil.msbFromBytes(ret);
        return new java.util.UUID(msb, lsb);
    }

    static byte[] nextBytes() {
        final long c = counter.incrementAndGet();
        final long t = System.currentTimeMillis() ^ start;
        final byte[] bytes = UUIDUtil.toBytes(c, t);

        // encrypt
        final byte[] ret;
        try {
        	ret = cipher.get().doFinal(bytes);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }

        ret[6] &= 0x0f;  /* clear version        */
        ret[6] |= 0x40;  /* set to version 4     */
        ret[8] &= 0x3f;  /* clear variant        */
        ret[8] |= 0x80;  /* set to IETF variant  */
        return ret;
    }
}
