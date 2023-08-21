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
  - isDone  轮询的方式会耗费无谓的cou资源，而且也不见得能及时得到结果。
    - 如果想要异步的获取结果，通常都会以轮询的方式去获取结果尽量不要阻塞。
