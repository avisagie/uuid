package asv.uuid;

import java.util.UUID;

/**
 * Utilities to ease creation, parsing and serialization of java.util.UUID
 * objects. Includes some fancy UUID generators with additional benefits.
 */
public class UUIDUtil {
    /**
     * Generate random UUIDs but much faster and without the
     * risk of exhausting Linux's entropy pool.
     * 
     * Uses AES in counter mode. Starts with a SecureRandom IV.
     *
     * @return a new UUID
     */
    public static java.util.UUID random() {
        return RandomUUIDGenerator.nextJava();
    }

    /**
     * Generate UUIDs that have better uniqueness behaviour than random
     * UUIDs in addition to being sortable by generation time.
     *
     * @return a new UUID
     */
    public static java.util.UUID epoch() {
        return Sha1Generator.generateUniqueJava(true, System.currentTimeMillis());
    }

    /**
     * Generate UUIDs that have better uniqueness behaviour than random
     * UUIDs in addition to being sortable by generation time. You get to
     * specify the timestamp.
     *
     * @param timestamp A timestamp to include in the UUID.
     * @return a new UUID
     */
    public static java.util.UUID epoch(long timestamp) {
        return Sha1Generator.generateUniqueJava(true, timestamp);
    }

    /**
     * Generate UUIDs that should have better uniqueness behaviour than random
     * UUIDs by virtue of hashing the mac addresses of the host together with a 
     * counter. Each machine should generate a subset of the space. 
     * Holding thumbs...  
     *
     * @return a new UUID
     */
    public static java.util.UUID uniquer() {
        return Sha1Generator.generateUniqueJava(false, System.currentTimeMillis());
    }

    public static java.util.UUID uuid5(byte[] name) {
        return Sha1Generator.generate5Java(name);
    }

    /**
     * Include a timestamp as the first four bytes of the uuid. This makes it sortable in time.
     * 
     * Excludes the timestamp from the hash.
     * 
     * @param name The raw data represented by the UUID.
     * @param timestamp A timestamp to include in the UUID.
     * @return a new UUID
     */
    public static java.util.UUID uuid5epoch(byte[] name, long timestamp) {
        return Sha1Generator.generate5Java(name, timestamp);
    }

    static int lexicographicCompare(long amsb, long alsb, long bmsb, long blsb) {
        // alas! no unsigneds
        long a1 = amsb >>> 32;
        long b1 = bmsb >>> 32;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        a1 = amsb & 0xFFFFFFFFL;
        b1 = bmsb & 0xFFFFFFFFL;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        a1 = alsb >>> 32;
        b1 = blsb >>> 32;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        a1 = alsb & 0xFFFFFFFFL;
        b1 = blsb & 0xFFFFFFFFL;
        if (a1 > b1) return +1;
        if (a1 < b1) return -1;

        return 0;
    }

    public static java.util.Comparator<java.util.UUID> lexicographicComparator() {
        return UUIDLexicographicComparator.instance;
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

    public static byte[] toBytes(UUID uuid) {
        return toBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public static UUID fromBytes(byte[] bytes) {
        return new UUID(msbFromBytes(bytes), lsbFromBytes(bytes));
    }
}
