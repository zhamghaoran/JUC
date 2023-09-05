package org.jjking.lock;

/**
 * @author 20179
 */
public class LockSyncDemo {
    Object obj = new Object();

    public void m1() {
        synchronized (obj) {
            System.out.println("----hello synchronized code block");
        }
    }

    public static void main(String[] args) {

    }
}
