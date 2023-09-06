package org.jjking.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author 20179
 */
public class DeadLockDemo {
    public static void main(String[] args) {
        final Object obj1 = new Object();
        final Object obj2 = new Object();
        new Thread(() -> {
            synchronized (obj1) {
                System.out.println("已拥有a，想拥有b");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (obj2) {
                    System.out.println(Thread.currentThread().getName() + "成功拥有b锁");
                }
            }
        }, "a").start();
        new Thread(() -> {
            synchronized (obj2) {
                System.out.println("已拥有b，想拥有a");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (obj1) {
                    System.out.println(Thread.currentThread().getName() + "成功拥有a锁");
                }
            }
        }, "b").start();
    }
}
