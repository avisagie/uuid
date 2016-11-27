package com.github.avisagie.uuid;

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
