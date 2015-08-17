package asv.uuid;

import java.io.Serializable;
import java.text.ParseException;
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
        return String.format("%08x-%04x-%04x-%04x-%012x",
                (msb >> 32) & 0xFFFFFFFFL,
                (msb >> 16) & 0xFFFFL,
                (msb >> 0) & 0xFFFFL,
                (lsb >> 48) & 0xFFFFL,
                (lsb >> 0) & 0xFFFFFFFFFFFFL);
    }

    private static final Pattern splitter = Pattern.compile("-");

    public static UUID fromString(String s) throws ParseException {
        final String[] strings;
        if (s.startsWith("{")) {
            strings = splitter.split(s.replace("{", "").replace("}", ""));
        } else {
            strings = splitter.split(s);
        }

        if (strings.length != 5) {
            throw new ParseException("Not a UUID: " + s, 0);
        }

        return new UUID(
                (Long.parseLong(strings[0], 16) << 32)
                        | (Long.parseLong(strings[1], 16) << 16)
                        | (Long.parseLong(strings[2], 16) << 0),
                (Long.parseLong(strings[3], 16) << 48)
                        | (Long.parseLong(strings[4], 16) << 0));
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
     * @return -1 for <, +1 for >, 0 for ==
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

    public static UUID randomUUID() {
        return random();
    }

    /**
     * Generates a UUID5 like UUID via SHA1 hashing of MACs, a counter and a random
     * seed. Fixes the first 32 bits to a timestamp so that the UUIDs in
     * lexicographic order matches time order. The timestamp component will
     * wrap in the year 2106.
     */
    public static UUID epoch() {
        return Sha1Generator.generateUnique(true);
    }

    /**
     * Generates a UUID5 like UUID via SHA1 hashing of time, counter, random seed and MACs.
     * It should have better uniqueness properties than the random uuid by tying the UUID
     * to unique time and space.
     */
    public static UUID sha1() {
        return Sha1Generator.generateUnique(false);
    }

    /**
     * Generates a UUID5 using a name and sha1. The first 16 bytes from the
     * resulting sha1 hash is returned with the appropriate version and variant
     * set.
     */
    public static UUID name(byte[] name) {
        return Sha1Generator.generate5(name);
    }

    public static UUID nameUUIDFromBytes(byte[] name) {
        return Sha1Generator.generate5(name);
    }
}
