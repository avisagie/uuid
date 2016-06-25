package za.asv.uuid;

import za.asv.uuid.UUIDUtil;

public class TryUUID {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(UUIDUtil.random());
        System.out.println(UUIDUtil.random());
        System.out.println(UUIDUtil.random());
        System.out.println(UUIDUtil.random());

        System.out.println(UUIDUtil.epoch());
        Thread.sleep(400);
        System.out.println(UUIDUtil.epoch());
        Thread.sleep(400);
        System.out.println(UUIDUtil.epoch());
        Thread.sleep(400);
        System.out.println(UUIDUtil.epoch());
        Thread.sleep(400);
    }
}
