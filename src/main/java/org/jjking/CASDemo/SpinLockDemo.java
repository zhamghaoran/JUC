package org.jjking.CASDemo;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/*
题目：实现一个自旋锁，复习CAS思想
自旋锁的好处： 循环比较获取类似wait的阻塞

通过CAS操作完成自旋锁，A线程先进来调用MyLock 方法自己持有锁5秒钟，B随后来进来发现
当前有现成持有锁，所以只能通过自旋等待，直到A释放锁后B随后抢到
 */
public class SpinLockDemo {

    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock() {
        Thread thread = Thread.currentThread();
        while (!atomicReference.compareAndSet(null, thread)) {
        }
        System.out.println(Thread.currentThread().getName() + "come in");
    }

    public void unlock() {
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread, null);
        System.out.println(thread.getName() + "task over");
    }

    public static void main(String[] args) throws InterruptedException {
        SpinLockDemo spinLockDemo = new SpinLockDemo();
        new Thread(() -> {
            spinLockDemo.lock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            spinLockDemo.unlock();
        }, "A").start();
        // 暂停500毫秒A先启动
        TimeUnit.MILLISECONDS.sleep(500);
        new Thread(() -> {
            spinLockDemo.lock();
            spinLockDemo.unlock();
        }, "B").start();
    }
}
