package asv.uuid;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class UUID implements Serializable, Comparable<UUID> {

    private static final Logger log = Logger.getLogger(UUID.class.getCanonicalName());

    static final long serialVersionUID = 29834756L;

    private final long msb, lsb;

    public UUID(long msb, long lsb) {
        this.msb = msb;
        this.lsb = lsb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UUID uuid = (UUID) o;

        if (msb != uuid.msb) return false;
        return lsb == uuid.lsb;
    }

    @Override
    public int hashCode() {
        int result = (int) (msb ^ (msb >>> 32));
        result = 31 * result + (int) (lsb ^ (lsb >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final byte[] bytes = toBytes(msb, lsb);
        return String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
                bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7],
                bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]);
    }

    private static final Pattern parse = Pattern.compile("\\{?([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})-([0-9a-f]{2})([0-9a-f]{2})-([0-9a-f]{2})([0-9a-f]{2})-([0-9a-f]{2})([0-9a-f]{2})-([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})\\}?");

    public static UUID fromString(String s) throws ParseException {
        final Matcher match = parse.matcher(s.toLowerCase());
        if (!match.matches()) {
            throw new ParseException("not a uuid: " + s, 0);
        }
        if (match.groupCount() != 16) {
            throw new ParseException("not a uuid: " + s, 0);
        }
        final byte[] bytes = new byte[16];
        for (int ii = 0; ii < 16; ii++) {
            bytes[ii] = (byte) (Integer.parseInt(match.group(ii + 1), 16) & 0xff);
        }
        return new UUID(msbFromBytes(bytes), lsbFromBytes(bytes));
    }

    public byte[] toBytes() {
        return toBytes(msb, lsb);
    }

    public static UUID fromBytes(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("UUID must be 16 bytes. This is " + bytes.length);
        }
        return new UUID(msbFromBytes(bytes), lsbFromBytes(bytes));
    }

    public java.util.UUID toJava() {
        return new java.util.UUID(msb, lsb);
    }

    public static UUID fromJava(java.util.UUID uuid) {
        return new UUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /**
     * Lexicographic unsigned byte-wise comparison.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(UUID o) {
        // alas! no unsigneds
        long a1 = msb >>> 32;
        long b1 = o.msb >>> 32;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        a1 = msb & 0xFFFFFFFFL;
        b1 = o.msb & 0xFFFFFFFFL;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        a1 = lsb >>> 32;
        b1 = o.lsb >>> 32;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        a1 = lsb & 0xFFFFFFFFL;
        b1 = o.lsb & 0xFFFFFFFFL;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        return 0;
    }

    private static class RandomUUIDGenerator {
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
                final SecureRandom rand = SecureRandom.getInstanceStrong();
                counter = new AtomicLong(rand.nextLong());
                start = rand.nextLong();

                final byte[] keyBytes = new byte[16];
                rand.nextBytes(keyBytes);
                skeySpec = new SecretKeySpec(keyBytes, "AES");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static UUID next() {
            final long c = counter.incrementAndGet();
            final long t = System.currentTimeMillis() ^ start;
            final byte[] bytes = toBytes(c, t);

            // encrypt
            final byte[] ret;
            synchronized (cipher) {
                try {
                    ret = cipher.get().doFinal(bytes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            ret[6] &= 0x0f;  /* clear version        */
            ret[6] |= 0x40;  /* set to version 4     */
            ret[8] &= 0x3f;  /* clear variant        */
            ret[8] |= 0x80;  /* set to IETF variant  */

            final long lsb = lsbFromBytes(ret);
            final long msb = msbFromBytes(ret);

            return new UUID(msb, lsb);
        }
    }

    static byte[] toBytes(long msb, long lsb) {
        final byte[] bytes = new byte[16];

        bytes[0] = (byte) ((msb >> 56) & 0xFFL);
        bytes[1] = (byte) ((msb >> 48) & 0xFFL);
        bytes[2] = (byte) ((msb >> 40) & 0xFFL);
        bytes[3] = (byte) ((msb >> 32) & 0xFFL);
        bytes[4] = (byte) ((msb >> 24) & 0xFFL);
        bytes[5] = (byte) ((msb >> 16) & 0xFFL);
        bytes[6] = (byte) ((msb >> 8) & 0xFFL);
        bytes[7] = (byte) ((msb >> 0) & 0xFFL);

        bytes[8] = (byte) ((lsb >> 56) & 0xFFL);
        bytes[9] = (byte) ((lsb >> 48) & 0xFFL);
        bytes[10] = (byte) ((lsb >> 40) & 0xFFL);
        bytes[11] = (byte) ((lsb >> 32) & 0xFFL);
        bytes[12] = (byte) ((lsb >> 24) & 0xFFL);
        bytes[13] = (byte) ((lsb >> 16) & 0xFFL);
        bytes[14] = (byte) ((lsb >> 8) & 0xFFL);
        bytes[15] = (byte) ((lsb >> 0) & 0xFFL);
        return bytes;
    }

    static long lsbFromBytes(byte[] ret) {
        return ((ret[8] & 0xFFL) << 56) |
                ((ret[9] & 0xFFL) << 48) |
                ((ret[10] & 0xFFL) << 40) |
                ((ret[11] & 0xFFL) << 32) |
                ((ret[12] & 0xFFL) << 24) |
                ((ret[13] & 0xFFL) << 16) |
                ((ret[14] & 0xFFL) << 8) |
                ((ret[15] & 0xFFL) << 0);
    }

    static long msbFromBytes(byte[] ret) {
        return ((ret[0] & 0xFFL) << 56) |
                ((ret[1] & 0xFFL) << 48) |
                ((ret[2] & 0xFFL) << 40) |
                ((ret[3] & 0xFFL) << 32) |
                ((ret[4] & 0xFFL) << 24) |
                ((ret[5] & 0xFFL) << 16) |
                ((ret[6] & 0xFFL) << 8) |
                ((ret[7] & 0xFFL) << 0);
    }

    /**
     * Replacement for java.util.UUID.randomUUID(). Faster and does not touch
     * the OS secure random entropy pool via SecureRandom.
     *
     * @return
     */
    public static UUID random() {
        return RandomUUIDGenerator.next();
    }

    /**
     * Generates a UUID5 like UUID via SHA1 hashing of time, MACs and a random
     * seed. Fixes the first 32 bits to a timestamp so that the UUIDs in
     * lexicographic order matches time order. The timestamp component will
     * wrap in the year 2106.
     */
    public static UUID epoch() {
        return Sha1Generator.generate(true);
    }

    /**
     * Generates a UUID5 like UUID via SHA1 hashing of time, random seed and MACs. It should have better
     * uniqueness properties than the random uuid by tying the UUID to unique time and space.
     */
    public static UUID sha1() {
        return Sha1Generator.generate(false);
    }

    static private final class Sha1Generator {
        static ThreadLocal<MessageDigest> sha1s = new ThreadLocal<MessageDigest>() {
            @Override
            protected MessageDigest initialValue() {
                try {
                    return MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException(e);
                }
            }
        };

        static final byte[] macs;
        static final SecureRandom rnd = new SecureRandom();
        static final AtomicLong counter = new AtomicLong(rnd.nextLong());

        static {
            macs = init();
        }

        private static byte[] init() {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                final Enumeration<NetworkInterface> iter = NetworkInterface.getNetworkInterfaces();
                while (iter.hasMoreElements()) {
                    final NetworkInterface nif = iter.nextElement();
                    final byte[] a = nif.getHardwareAddress();
                    if (a != null) {
                        out.write(a);
                    }
                }
            } catch (SocketException e) {
                log.log(Level.WARNING, "Error getting mac addresses:", e);
                return random().toBytes();
            } catch (IOException e) {
                throw new IllegalStateException("Impossible", e);
            }

            // Less than this is an issue. Use a random "mac".
            if (out.size() < 6) {
                return random().toBytes();
            }

            // Some hosts have lots of NICs. It slows things down and does not
            // really contribute anything. At least shorten to 20.
            if (out.size() > 20) {
                final MessageDigest digest = sha1s.get();
                return digest.digest(out.toByteArray());
            }

            return out.toByteArray();
        }

        static UUID generate(boolean includeEpoch) {
            final long now = System.currentTimeMillis();

            final MessageDigest digest = sha1s.get();
            digest.reset();

            digest.update(macs);
            if (!includeEpoch) digestLong(digest, now);
            digestLong(digest, counter.incrementAndGet());

            final byte[] hash = digest.digest();

            if (includeEpoch) {
                final long epoch = now / 1000L;
                hash[0] = (byte) ((epoch >>> 24) & 0xFF);
                hash[1] = (byte) ((epoch >>> 16) & 0xFF);
                hash[2] = (byte) ((epoch >>> 8) & 0xFF);
                hash[3] = (byte) ((epoch >>> 0) & 0xFF);
            }

            return UUID.fromBytes(Arrays.copyOf(hash, 16));
        }

        private static void digestLong(MessageDigest digest, long c) {
            for (int ii = 0; ii < 8; ii++) {
                digest.update((byte) ((c >>> (ii * 8)) & 0xFF));
            }
        }
    }
}
