package org.jjking.Volatile;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class Mynumber {
    volatile int number;

    public synchronized void addPlus() {
        number++;
    }
}

public class volatileAtomicDemo {
    public static void main(String[] args) throws InterruptedException {
        Mynumber mynumber = new Mynumber();
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                for (int j = 1; j <= 1000; j++) {
                    mynumber.addPlus();
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(2);

        System.out.println(mynumber.number);
    }
}
