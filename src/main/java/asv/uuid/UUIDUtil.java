package asv.uuid;

/**
 * Utilities to get the benefits of UUID with java.util.UUID.
 */
public class UUIDUtil {
    /**
     * Generate random UUIDs but much faster and without the
     * risk of exhausting Linux's entropy pool.
     * @return
     */
    public static java.util.UUID randomUUID() {
        return RandomUUIDGenerator.nextJava();
    }

    /**
     * Generate UUIDs that have better uniqueness gaurantees than random
     * UUIDs in addition to being sortable by generation time.
     * @return
     */
    public static java.util.UUID epoch() {
        return Sha1Generator.generateUniqueJava(true);
    }

    public static java.util.UUID uuid5(byte[] name) {
        return Sha1Generator.generate5Java(name);
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
}
