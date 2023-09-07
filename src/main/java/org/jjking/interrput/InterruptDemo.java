package org.jjking.interrput;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 20179
 */
public class InterruptDemo {
    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        m3();
    }

    private static void m1() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (isStop) {
                    System.out.println(Thread.currentThread().getName() + " isStop 的值被修改为true 程序停止");
                    break;
                }
                System.out.println("----hello volatile");
            }
        }, "a").start();
        TimeUnit.MILLISECONDS.sleep(20);
        new Thread(() -> {
            isStop = true;
        }, "b").start();
    }

    private static void m2() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (atomicBoolean.get()) {
                    System.out.println(Thread.currentThread().getName() + " atomicBoolean 的值被修改为true 程序停止");
                    break;
                }
                System.out.println("----hello atomicBoolean");
            }
        }, "a").start();
        TimeUnit.MILLISECONDS.sleep(20);

        new Thread(() -> {
            atomicBoolean.set(true);
        }, "b").start();
    }

    public static void m3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " is interrupt 被修改为true，程序停止");
                    break;
                }
                System.out.println("t1 ------  hello interrupt api");
            }
        }, "a");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(20);
        new Thread(t1::interrupt).start();
    }
}
