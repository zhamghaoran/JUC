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





