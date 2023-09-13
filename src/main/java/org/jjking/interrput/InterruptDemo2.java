package org.jjking.interrput;

import java.util.concurrent.TimeUnit;

/**
 * @author 20179
 */
public class InterruptDemo2 {
    public static void main(String[] args) throws InterruptedException {
        // 实例方法interrupt() 仅仅是设置线程的中断状态为true，不会停止线程
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 3000; i++) {
                System.out.println("-----");
            }
            System.out.println("t1线程默认的中断标志02: " + Thread.currentThread().isInterrupted()); // true
        }, "t1");
        t1.start();
        System.out.println("t1线程默认的中断标志: " + t1.isInterrupted()); // false
        t1.interrupt();
        System.out.println("t1线程默认的中断标志01: " + t1.isInterrupted()); // true
        t1.join();
        System.out.println("t1线程默认的中断标志03: " + t1.isInterrupted()); // true
    }
}
