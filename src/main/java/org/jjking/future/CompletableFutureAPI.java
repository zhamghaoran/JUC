package org.jjking.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPI {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "abc";
        });
//        TimeUnit.SECONDS.sleep(2);
//        System.out.println(completableFuture.getNow("test"));
        completableFuture.complete("123");
        System.out.println(completableFuture.get());
    }
}
