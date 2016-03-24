package asv.uuid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by albert on 2015/08/16.
 */
final class Sha1Generator {
    private static final Logger log = Logger.getLogger(Sha1Generator.class.getCanonicalName());

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
    static final AtomicLong counter;

    static {
        final SecureRandom rnd = new SecureRandom();
        counter = new AtomicLong(rnd.nextLong());
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
            return RandomUUIDGenerator.nextBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible", e);
        }

        // Less than this is an issue. Use a random "mac".
        if (out.size() < 6) {
            return RandomUUIDGenerator.nextBytes();
        }

        // Some hosts have lots of NICs. It slows things down and does not
        // really contribute anything. At least shorten to 20.
        if (out.size() > 20) {
            final MessageDigest digest = sha1s.get();
            return digest.digest(out.toByteArray());
        }

        return out.toByteArray();
    }

    static java.util.UUID generate5Java(byte[] name) {
        return UUIDUtil.fromBytes(generate5Bytes(name, false, 0L));
    }

    static java.util.UUID generate5Java(byte[] name, long time) {
        return UUIDUtil.fromBytes(generate5Bytes(name, true, time));
    }

    private static byte[] generate5Bytes(byte[] name, boolean includeEpoch, long time) {
        final MessageDigest digest = sha1s.get();
        digest.reset();
        digest.update(name);
        final byte[] hash = digest.digest();

        hash[6] &= 0x0f;
        hash[6] |= 0x50;  // version 5
        hash[8] &= 0x3f;
        hash[8] |= 0x80;

        if (includeEpoch) {
            final long epoch = time / 1000L;
            hash[0] = (byte) ((epoch >>> 24) & 0xFF);
            hash[1] = (byte) ((epoch >>> 16) & 0xFF);
            hash[2] = (byte) ((epoch >>> 8) & 0xFF);
            hash[3] = (byte) ((epoch >>> 0) & 0xFF);
        }

        return Arrays.copyOf(hash, 16);
    }

    static java.util.UUID generateUniqueJava(boolean includeEpoch, long timestamp) {
        final byte[] bytes = generateUniqueBytes(includeEpoch, timestamp);
        return UUIDUtil.fromBytes(bytes);
    }

    private static byte[] generateUniqueBytes(boolean includeEpoch, long timestamp) {
        final MessageDigest digest = sha1s.get();
        digest.reset();

        digest.update(macs);
        if (!includeEpoch) digestLong(digest, timestamp);
        digestLong(digest, counter.incrementAndGet());

        final byte[] hash = digest.digest();

        if (includeEpoch) {
            final long epoch = timestamp / 1000L;
            hash[0] = (byte) ((epoch >>> 24) & 0xFF);
            hash[1] = (byte) ((epoch >>> 16) & 0xFF);
            hash[2] = (byte) ((epoch >>> 8) & 0xFF);
            hash[3] = (byte) ((epoch >>> 0) & 0xFF);
        }

        return Arrays.copyOf(hash, 16);
    }

    private static void digestLong(MessageDigest digest, long c) {
        for (int ii = 0; ii < 8; ii++) {
            digest.update((byte) ((c >>> (ii * 8)) & 0xFF));
        }
    }
}
