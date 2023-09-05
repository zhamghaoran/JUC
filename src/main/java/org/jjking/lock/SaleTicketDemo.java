package org.jjking.lock;

import java.lang.reflect.Type;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 20179
 */
class Ticket {
    private int number = 50;
    ReentrantLock lock = new ReentrantLock(true);

    public void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出第" + number--);
            }
        } finally {
            lock.unlock();
        }
    }
}

/**
 * @author 20179
 */
public class SaleTicketDemo {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        new Thread(() -> {
            for (int i = 0; i < 51; i++) {
                ticket.sale();
            }
        }, "a").start();
        new Thread(() -> {
            for (int i = 0; i < 51; i++) {
                ticket.sale();
            }
        }, "b").start();
        new Thread(() -> {
            for (int i = 0; i < 51; i++) {
                ticket.sale();
            }
        }, "c").start();
    }
}
