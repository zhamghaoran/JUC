package org.jjking.future;

import java.util.concurrent.CompletableFuture;

/**
 * @author 20179
 */
public class CompletableFutureAPI3 {

    public static void main(String[] args) {
        CompletableFuture.supplyAsync(
                        () -> 1
                )
                .thenApply(v -> v + 1)
                .thenAccept(System.out::println);
    }
}
