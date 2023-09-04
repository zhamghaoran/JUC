package org.jjking.future;

import java.util.concurrent.*;

/**
 * @author 20179
 */
public class CompletableFutureAPI2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("1111");
            return 1;
        }, threadPool).thenApply(f -> {
            System.out.println("2222");
            return f + 2;
        }).thenApply(f -> {
            System.out.println("3333");
            return f + 3;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("计算结果：" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }).get();


        threadPool.shutdown();
    }
}
