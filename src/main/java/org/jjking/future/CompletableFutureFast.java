package org.jjking.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author 20179
 */
public class CompletableFutureFast {
    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("a come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "play1";
        });
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("b come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "play2";
        });
        CompletableFuture<String> completableFuture2 = completableFuture1.applyToEither(completableFuture, (f) -> {
            return f + "is winner";
        });
        System.out.println(completableFuture2.join());
    }
}
