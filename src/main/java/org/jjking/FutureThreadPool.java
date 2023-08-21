package org.jjking;

import java.beans.beancontext.BeanContext;
import java.util.concurrent.*;

/**
 * @author 20179
 */
public class FutureThreadPool {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // 三个任务，目前开启对个异步线程来处，请问耗时多少
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            TimeUnit.MILLISECONDS.sleep(500);
            return "task 1 finish";
        });
        FutureTask<String> futureTask2 = new FutureTask<>(() -> {
            TimeUnit.MILLISECONDS.sleep(300);
            return "task 2 finish";
        });
        executorService.submit(futureTask);
        executorService.submit(futureTask2);
        TimeUnit.MILLISECONDS.sleep(300);
        System.out.println(futureTask.get());
        System.out.println(futureTask2.get());
        executorService.shutdown();
        long endTime = System.currentTimeMillis();
        System.out.println("cost time: ---" + (endTime - startTime));
        System.out.println(Thread.currentThread().getName() + "\t" + "--- end");
    }

    private static void m1() throws InterruptedException {
        // 三个任务，目前只有一个线程main来处理，请问耗时多少
        long startTime = System.currentTimeMillis();
        TimeUnit.MILLISECONDS.sleep(500);
        TimeUnit.MILLISECONDS.sleep(300);
        TimeUnit.MILLISECONDS.sleep(300);
        long endTime = System.currentTimeMillis();
        System.out.println("cost time: ---" + (endTime - startTime));
        System.out.println(Thread.currentThread().getName() + "\t" + "--- end");
    }
}
