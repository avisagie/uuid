package asv.uuid;

/**
 * Created by albert on 2015/06/07.
 */
public class TryUUID {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(UUID.random());
        System.out.println(UUID.random());
        System.out.println(UUID.random());
        System.out.println(UUID.random());

        System.out.println(UUID.epoch());
        Thread.sleep(400);
        System.out.println(UUID.epoch());
        Thread.sleep(400);
        System.out.println(UUID.epoch());
        Thread.sleep(400);
        System.out.println(UUID.epoch());
        Thread.sleep(400);
    }
}
