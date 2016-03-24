package asv.uuid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

import static asv.uuid.UUIDUtil.*;

public class GenerateMany {
    private static UUID me = UUIDUtil.randomUUID();

    public static void main(String[] args) throws Exception {
        // init everything so long
        uniquer();

        for (;;) {
            genEpoch();
            System.err.flush();
            System.out.flush();
        }
    }

    private static void check() {

    }

    private static void genRandom() throws Exception {
        int NUM = 1000000;

        final ArrayList<UUID> uuids = new ArrayList<>(NUM);
        System.err.println("Generating " + (System.currentTimeMillis() % 5000));
        System.err.flush();
        for (int i = 0; i < NUM; i++) {
            uuids.add(randomUUID());
        }

        System.err.println("Sorting");
        System.err.flush();
        Collections.sort(uuids, lexicographicComparator());

        System.err.println("Writing");
        System.err.flush();

        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(randomUUID() + ".txt")))) {
            for (UUID x : uuids) {
                out.write(x.toString().getBytes());
                out.write("\n".getBytes());
            }
        }

        System.err.println("Done");
        System.err.flush();
    }

    private static final Executor exec = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(1),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static void genEpoch() throws Exception {
        int NUM = 10000;

        // Wait for the next 10s boundary to sync with others
        final long now = System.currentTimeMillis();
        final long start = (now / 1000L + 1) * 1000L;
        System.err.println("Now: " + new Date(now) + " will start at " + new Date(start));
        Thread.sleep(start - now);

        final ArrayList<UUID> uuids = new ArrayList<>(NUM);
        System.err.println("Generating " + (System.currentTimeMillis() % 1000));
        System.err.flush();
        for (int i = 0; i < NUM; i++) {
            uuids.add(epoch());
        }

        exec.execute(() -> {
            System.err.println("Sorting");
            System.err.flush();
            Collections.sort(uuids, lexicographicComparator());

            System.err.println("Writing");
            System.err.flush();

            final File dir = new File(Long.toString(now / 1000));
            dir.mkdirs();

            try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(dir, me + ".txt")))) {
                for (UUID x : uuids) {
                    out.write(x.toString().getBytes());
                    out.write("\n".getBytes());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            System.err.println("Done");
            System.err.flush();
        });
    }
}
