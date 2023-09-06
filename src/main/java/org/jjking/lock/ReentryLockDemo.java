package org.jjking.lock;

/**
 * @author 20179
 */
public class ReentryLockDemo {



    public static void main(String[] args) {
        m1();
    }
    public static void m1() {
        final Object obj = new Object();
        new Thread(() -> {
            synchronized (obj) {
                System.out.println(Thread.currentThread().getName() + "外层调用");
                synchronized (obj) {
                    System.out.println(Thread.currentThread().getName() + "中层调用");
                    synchronized (obj) {
                        System.out.println(Thread.currentThread().getName() + "内层调用");
                    }
                }
            }
        }, "t1").start();
    }
}
