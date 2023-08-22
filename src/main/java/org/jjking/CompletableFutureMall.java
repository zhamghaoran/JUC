package org.jjking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CompletableFutureMall {
    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("dangdang"),
            new NetMall("taobao")
    );

    public static List<String> getPrice(List<NetMall> list, String productName) {
        return list
                .stream()
                .map(netMall ->
                        String.format(productName + " in %s + price: + %.2f",
                                netMall.getNetMallName(),
                                netMall.calcPrice(productName)))
                .toList();

    }

    public static List<String> getPriceByCompletableFuture(List<NetMall> list, String productName) {
        return list
                .stream()
                .map(netMall ->
                        CompletableFuture.supplyAsync(() ->
                                String.format(productName + " in %s + price: + %.2f",
                                        netMall.getNetMallName(),
                                        netMall.calcPrice(productName))))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .toList();

    }

    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
//        getPrice(list, "mysql").forEach(System.out::println);
//        long endTime = System.currentTimeMillis();
//        System.out.println(endTime - startTime);

        long startTime = System.currentTimeMillis();
        getPriceByCompletableFuture(list, "mysql").forEach(System.out::println);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class NetMall {
    private String netMallName;

    public double calcPrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}
