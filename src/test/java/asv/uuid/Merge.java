package asv.uuid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Merge {
    private static final int NUM = 10000;

    public static void main(String[] args) throws Exception {
        // opens files concurrently. Reads from each in batches, sorts, prints only duplicates.
        final List<BufferedReader> files = Arrays.stream(args)
                .map((fn) -> {
                    try {
                        return new BufferedReader(new FileReader(fn));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        ArrayList<String> thisBatch = new ArrayList<>(args.length * NUM);
        ArrayList<String> nextBatch = new ArrayList<>(args.length * NUM);

        boolean done = false;
        long total = 0;
        while (!done) {
            {
                final ArrayList<String> tmp = thisBatch;
                thisBatch = nextBatch;
                nextBatch = tmp;
                nextBatch.clear();
            }

            final Iterator<BufferedReader> iter = files.iterator();

            // read NUM from the first file
            BufferedReader in = iter.next();
            String line1 = "";
            for (int ii = 0; ii < NUM; ii++) {
                line1 = in.readLine();
                if (line1 == null) {
                    System.err.println("last");
                    done = true;
                    break;
                }

                thisBatch.add(line1);
            }

            System.err.println("Got to " + line1);

            // read from each subsequent file until the first line that is greater than
            // the last line from the first file. If the first file is finished
            outer:
            while (iter.hasNext()) {
                in = iter.next();
                String line;
                while ((line = in.readLine()) != null) {
                    if (line1 != null && line.compareTo(line1) > 0) {
                        nextBatch.add(line);
                        continue outer;
                    }
                    thisBatch.add(line);
                }
            }

            // sort and find duplicates
            System.err.println("Checking " + thisBatch.size());
            total += thisBatch.size();
            Collections.sort(thisBatch);
            String prev = null;
            for (String x: thisBatch) {
                if (prev != null && prev.equals(x)) {
                    System.out.println(x);
                }
                prev = x;
            }
        }

        System.out.println("Total lines: " + total);
        System.out.flush();
    }
}
