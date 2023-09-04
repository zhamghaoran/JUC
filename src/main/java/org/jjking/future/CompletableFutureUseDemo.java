package org.jjking.future;

import java.util.concurrent.*;

/**
 * @author 20179
 */
public class CompletableFutureUseDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "----- come in");
                int res = ThreadLocalRandom.current().nextInt(10);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------一秒钟后出结果" + res);
                return res;
            }, threadPool).whenComplete((v, e) -> {
                if (e == null) {
                    System.out.println("----------- 计算完成，更新系统Update:" + v);
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println("异常情况：" + e.getCause() + e.getMessage());
                return null;
            });
            System.out.println(Thread.currentThread().getName() + "----- 先去忙其他的");
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
        } finally {
            threadPool.shutdownNow();
        }
    }

    private static void method1() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "----- come in");
            int res = ThreadLocalRandom.current().nextInt();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("-----------一秒钟后出结果" + res);
            return res;
        });
        System.out.println(Thread.currentThread().getName() + "----- 先去忙其他的");
        System.out.println(completableFuture.get());
    }
}
