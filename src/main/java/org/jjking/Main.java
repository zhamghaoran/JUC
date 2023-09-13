package org.jjking;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author 20179
 */
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> futureTask = new FutureTask<>(new Mythread());
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();
        System.out.println(futureTask.get());
    }

    public static String add(String a, String b) {
        return a + b;
    }
}

class Mythread implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("come in call()-----");
        return "hello callable";
    }

}