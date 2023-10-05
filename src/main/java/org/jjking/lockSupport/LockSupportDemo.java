package org.jjking.lockSupport;

import javax.swing.plaf.synth.SynthButtonUI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 20179
 */
public class LockSupportDemo {
    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "come in");
            LockSupport.park();
            System.out.println("被唤醒");
        }, "t1");
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            System.out.println("发出通知");
            LockSupport.unpark(thread);
        }).start();
        thread.start();
    }

    public static void syncWaitNotify() {
        Object o = new Object();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (o) {
                System.out.println(Thread.currentThread().getName() + "----");
                try {
                    o.notify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("被唤醒");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (o) {
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "发出通知");
            }
        }, "t2").start();
    }

    public static void lockAwaitSignal() {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();
        new Thread(() -> {
            reentrantLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "---come in");
                condition.await();
                System.out.println("被唤醒");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        }, "t1").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            reentrantLock.lock();
            try {
                condition.signal();
                System.out.println("发出通知");
            } finally {
                reentrantLock.unlock();
            }
        }, "t2").start();
    }
}
