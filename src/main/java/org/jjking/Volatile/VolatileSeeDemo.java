package org.jjking.Volatile;

import java.util.concurrent.TimeUnit;

public class VolatileSeeDemo {
    static volatile boolean flag = true;

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "   come in");
            while (flag) {
            }
            System.out.println(Thread.currentThread().getName() + "   flag 被设置为false 程序截止");
        }).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        flag = false;
        System.out.println(Thread.currentThread().getName() + "   main线程修改完成");
    }
}
