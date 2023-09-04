package org.jjking.lock;

import java.util.concurrent.TimeUnit;

// 资源类
class phone {
    public static synchronized void sendMail() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("发邮件");
    }

    public static synchronized void sendSMS() {
        System.out.println("发短信");
    }

    public void hello() {
        System.out.println("hello");
    }

}

public class LockDemo {
    public static void main(String[] args) throws InterruptedException {
        phone phone = new phone();
        phone phone1 = new phone();
        new Thread(() -> {
            phone.sendMail();
        }, "a").start();
        TimeUnit.SECONDS.sleep(2);
        new Thread(() -> {
            phone.hello();
        }, "b").start();

    }
}
