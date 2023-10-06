package org.jjking.CASDemo;

import java.util.concurrent.atomic.AtomicInteger;

public class CASDemo1 {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5, 2022) + "  " + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 2022) + "  " + atomicInteger.get());
        atomicInteger.getAndIncrement();
        System.out.println(atomicInteger.get());
    }
}
