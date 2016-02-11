package asv.uuid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class GenerateMany {
    private static final int NUM = 1000000;

    public static void main(String[] args) throws Exception {
        gen();
        System.err.flush();
        System.out.flush();
    }

    private static void check() {

    }

    private static void gen() throws Exception {
        // init everything so long
        UUIDUtil.random();
        UUIDUtil.randomUUID();

        // Wait for the next 10s boundary to sync with others
        long now = System.currentTimeMillis();
        long start = (now / 10000L + 1) * 10000L;
        System.err.println("Now: " + new Date(now) + " will start at " + new Date(start));
        Thread.sleep(start - now);
        ArrayList<UUID> uuids = new ArrayList<>(NUM);

        System.err.println("Generating " + (System.currentTimeMillis() % 10000));
        System.err.flush();
        for (int i = 0; i < NUM; i++) {
            uuids.add(UUIDUtil.random());
        }

        System.err.println("Sorting");
        System.err.flush();
        Collections.sort(uuids, UUIDUtil.lexicographicComparator());

        System.err.println("Writing");
        System.err.flush();
        for (UUID x : uuids) {
            System.out.println(x);
        }

        System.err.println("Done");
        System.err.flush();
    }
}
