# JUC

## 管程

Monitor 其实是一种同步机制，他的义务是保证(同一时间)只有一个线程可以访问被保护的数据和代码
JVM中同步是基于，进出和退出监视器对象(Monitor，管程对象)来实现的，每个对象实例都有一个Monitor对象
Monitor 对象和Java对象一同创建并销毁，它的底层是c++ 实现的

## Future

future接口可以为主线程开一个任务分支，专门为主线程处理耗时和费力的复杂业务。

- 优点: future + 线程池多线程任务配合，能显著提高程序执行效率
    - case:

```java
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
}
```

- 缺点:
    - get 方法阻塞，一旦调用get 方法，如果没有完成计算，就会造成程序阻塞
    - isDone 轮询的方式会耗费无谓的cou资源，而且也不见得能及时得到结果。
        - 如果想要异步的获取结果，通常都会以轮询的方式去获取结果尽量不要阻塞。

### 总结

对于简单的的业务场景， 使用future完全ok
但是对于较为复杂的任务：

- 多个异步任务的计算结果组合起来，后一个异步任务需要前一个的计算结果
- 当future集合中某个任务结束时，结果返回，返回第一名的结果

## CompletionStage

- CompletionStage 代表异步计算过程中的某一个阶段，一个阶段完成之后可能会触发另一个阶段
- 一个阶段的计算执行可能是一个function ，Consumer，或者Runnable。比如：stage.thenApply(x -> square(x)).thenAccept(x ->
  System.out.print(x)).thenRun(() -> System.out.println())
- 代表异步计算过程中的某一个阶段，一个阶段完成之后可能会触发另一个阶段，有些类似Linux系统的管道分隔符传参数

## CompletableFuture

- 在java8
  当中，CompletableFuture提供了非常强大的Future扩展功能，可以帮助我们简化异步编程的复杂性，并且提供了函数式编程的能力，可以通过回调的方式处理计算结果，也提供了转换和组合CompletableFuture的方法
- 它可能代表一个明确完成的Future，也可能代表一个完成阶段(completionStage),他可以支持在计算之后触发一些函数或执行某些动作。
- 它实现了Future和CompletionStage接口

```java
public static CompletableFuture<Void> runAsync(Runnable runnable,Executor executor){}
```

#### runAsync 方法创建一个没有返回值的CompletableFuture

- 第一个参数为一个Runnable 方法， 第二个参数可以传入自定义的线程池
- 使用completableFuture执行Runnable 使用默认线程池 方法：

```java
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(completableFuture.get());
    }
}
```

- 使用completableFuture执行Runnable 使用自定义线程池 方法：

```java
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        System.out.println(completableFuture.get());
        threadPool.shutdown();
    }
}
```

#### 有返回值的CompletableFuture

```java
public static<U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor){}
```

同理这里还是有两种方式可以创建CompletableFuture，一种是自定义线程池，一种是使用默认的线程池

```java
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hello supplyAsync";
        }, threadPool);
        System.out.println(completableFuture.get());
        threadPool.shutdown();
    }
}
```

#### CompletableFuture为什么比Future强大

- 异步结束时，会自动回调某个对象的方法
- 主线程设置好回调之后，不再关心异步任务的执行，异步任务可以按照顺序执行
- 异步任务出错的时候会自动回调某个对象的方法

#### CompletableFuture 链式调用

```java
public class CompletableFutureUseDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 使用自定义的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            // 开启一个异步调用
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
                // 上一个任务结束之后，继续完成后面的任务 v是上个任务的返回值，e上个任务中抛出的异常
            }, threadPool).whenComplete((v, e) -> {
                if (e == null) {
                    System.out.println("----------- 计算完成，更新系统Update:" + v);
                }
                // 创建一个异常处理方法
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println("异常情况：" + e.getCause() + e.getMessage());
                return null;
            });
            System.out.println(Thread.currentThread().getName() + "----- 先去忙其他的");
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
        } finally {
            // 使用结束之后关闭资源
            threadPool.shutdownNow();
        }
    }

}
```

#### CompletableFuture Mall 实战

```java
public class CompletableFutureMall {
    // 创建几个电商
    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("dangdang"),
            new NetMall("taobao")
    );

    // 运用传统的串行方法调用方法
    public static List<String> getPrice(List<NetMall> list, String productName) {
        return list
                .stream()
                .map(netMall ->
                        String.format(productName + " in %s + price: + %.2f",
                                netMall.getNetMallName(),
                                netMall.calcPrice(productName)))
                .toList();

    }

    // 使用CompletableFuture来异步处理方法
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
```

#### CompletableFuture 常用用法

- 获得结果和触发计算
  获取结果:
  ```java
    public T get() {}
    public T get(long timeout,TimeUnit unit) {} 
    public T join() {}
    // 如果在调用getNow 方法的时候还没有完成计算，那么就会返回valueIfAbsent的值，如果计算完成了就返回计算结果
    public T getNow(T valueifAbsent) {}
  ```
  触发计算:
  ```java
  // 如果没有完成计算，就用value作为get() 的返回值
  public boolean complete(T value){}
  ```
- 对计算结果进行合并
  ```java
     //接受上一步传入的参数，进行下一步的运算，第一个参数是上一步的结果，再返回一个值
     public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn) {}
    // 一旦在执行过程当中出现异常，就会跳到这个方法
     public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
    
  ```
- 对计算结果进行消费
  接受任务的处理结果，并消费处理，无返回结果

```java
// 没有返回值，传入上一步的结果
public CompletableFuture<Void> thenAccept(Consumer<? super T>action){}
```

- 对计算结果进行选用
- 对计算结果进行选用

```java
public class CompletableFutureFast {
    public static void main(String[] args) {
        // 先开启第一个任务
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("a come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "play1";
        });
        // 再开启第二个任务
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("b come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "play2";
        });
        // 从两个任务当中选取快的一个
        CompletableFuture<String> completableFuture2 = completableFuture1.applyToEither(completableFuture, (f) -> {
            return f + "is winner";
        });
        输出结果
        System.out.println(completableFuture2.join());
    }
}

```

- 对两个任务进行合并
  两个completionStage任务都完成之后，最终能把两个任务的结果一起交给thenCombine来处理
  先完成的先等着，等待其他分支任务
```java
package org.jjking;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author 20179
 */
public class CompletableFutureCombineDemo {
    public static void main(String[] args) {
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "启动");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10;
        });

        CompletableFuture<Integer> integerCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "启动");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 20;
        });
        CompletableFuture<Integer> res = integerCompletableFuture2.thenCombine(integerCompletableFuture, (x, y) -> {
            System.out.println("开始结果合并");
            return x + y;
        });
        System.out.println(res.join());
    }
}

```



