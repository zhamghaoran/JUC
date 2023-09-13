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

## 锁

### 乐观锁，悲观锁
- 悲观锁  认为自己在使用数据的时候一定有别的线程来修改数据，因此在获取数据的时候会先加锁，确保数据不会被别的数据修改。

  synchronized关键字和Lock的实现类都是悲观锁

- 乐观锁

  认为自己在使用数据时不会有别的线程来修改数据或资源，所以不会添加锁

  在java当中是通过无锁编程来实现，只是在更新数据的时候去判断，之前有没有别的线程更新了这个数据

  如果这个数据没有被更新，当前的线程，将自己的数据成功写入。

  如果这个数据已经被其他的线程更新，则根据不同的实现方式执行不同的操作，比如放弃修改，重试枪锁等等

  - 判断规则 

    版本号机制Version

    最常用的是CAS写法，Java原子类中的递增操作就通过CAS自旋实现的
## synchronized

- 一个对象当中如果有多个syn方法，某一个时刻，只要有一个线程去调用其中的一个syn方法了，其他的线程只能等待。

  换句话说，某一个时刻， 只能有唯一的一个方法去访问这些syn的方法

  锁拿的是当前对象this ，被锁定后，其他的线程都不能进入到当前对象的其他的syn方法

- 普通方法正常执行

- static 方法加锁会锁住类

- 对于普通的同步方法，锁的是当前的实例对象， 通常是指this，具体的一个个对象，所有的同步方法用的都是同一把锁---> 对象实例本身

- 对于静态同步方法快，锁的是当前的Class对象， 如Iphone.class 唯一的一个模板

- 对于同步方法快，锁的是syn括号内的对象

- 当一个线程试图访问同步锁的时候他必须先要获得锁，正常退出或抛出异常异常时必须释放锁
  当所有同步方法调用的都是同一把锁---实例对象本身，就是new出来的具体实例对象本身即this
  
  也就是说如果一个实例对象的普通同步方法获取锁之后，该实例对象的其他同步方法必须等待获取锁的方法释放锁之后才能获取锁
  
  所有的静态同步方法用的是同一把锁---类对象本身，就是我们说过的唯一的模板class
  
  具体实例对象this和唯一的模板class，这两把锁是两个不同的对象，所以静态同步方法与普通同步方法之间是不会有竞态条件的
  
  但是一旦一个静态同步方法获取锁后，其他的静态同步方法都必须等待该方法释放锁之后才能获取锁 
  
- 所有的静态同步方法用的也是同一把锁---类对象本身，就是我们说的唯一模板class

  具体的实例对象this 和唯一模板class，这两把锁是两个不同的对象，所以静态同步方法与普通方法之间是不会有竞态条件的

  但是一旦一个静态同步方法获取锁之后，其他的静态同步方法都必须等待释放锁之后才能获取锁
### 公平锁和非公平锁

- 公平锁  指多个线程按照申请锁的顺序来获取锁，这里类似排队买票，先来的人先买，后来的人在队尾排着，这是公平的 

  ```java
  Lock lock = new ReentryLock(true);  表示公平锁，先来先得
  ```

- 是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁，在高并发的环境下，有可能造成优先级翻转或者饥饿的状态(某一个线程一直得不到锁)

  ```java
  Lock lock = new ReentryLock(false);
  Lock lock = new ReentryLock(); // 默认是非公平锁
  ```

- 非公平锁的优点： 

  - 恢复挂起的线程到真正锁的获取还是有时间差的，从开发人员来看这个时间微乎其微，但是从CPU的角度来看，这个时间差的存在的还是很明显的。所以非公平锁能充分的利用CPU的时间片。尽量减少CPU的空闲时间状态
  - 使用多线程很重要的考量点事线程切换的开销，当采用非公平锁时，当一个线程请求锁获取同步状态，然后释放同步状态，所以刚释放锁的线程在此刻再次获取同步状态的概率就会变得非常大，所以就减少了线程的开销

### 可重入锁

是指在同一个线程的外层方法获取锁的时候，再进入该线程的内层方法会自动获取锁(前提，锁对象是同一个对象)，不会因为之前已经获取过还没释放而阻塞。

如果一个有synchronized 修饰的递归调用方法，程序第二次进入被自己阻塞了岂不是天大的笑话，出现了作茧自缚。

所以java中ReentryLock和synchronized  都是可重入锁，可重入锁的一个优点是可一定程度避免死锁。

- synchronized  是可重入锁
- ReentryLock 是可重入锁

### 死锁产生的原因

- 系统资源不足
- 进程运行推进的顺序不合适
- 资源分配不当

如何排查死锁 
方法一
- 先使用jsp -l 查看java 进程
- 再使用 jstack 进程编号  获得详细信息

方法二
- 使用jconsole 查看java控制台

### 小总结
指针指向monitor 对象 (也称为管程或监视器锁)的起始地址。每个对象都存在一个monitor 与之关联，当一个monitor 被某个线程持有之后，它便处于锁定状态。

在java虚拟机当(hotSpot)中，monitor 是有ObjectMonitor 实现的。

### 死锁(独占锁)/读锁(共享锁)

### 自旋锁SpinLock

### 无锁-> 独占锁 -> 读写锁-> 邮戳锁

### 无锁-> 偏向锁 -> 轻量锁 -> 重量锁

## LockSupport 和 线程中断

###  什么是中断机制

- 首先

  一个线程不应该由其他线程来强制中断或停止，而是应该由线程自己自行停止，自己来决定自己的命运

  所以，Thread.stop ,  Thread.suspend , Thread.resume 都已经被废弃了。

- 其次

  在java中没有办法立即停止一条线程，然而停止线程却显得尤为重要，如取消一个耗时操作

  因此， java提供了一种用于停止线程的协商机制----中断，也即中断标识协商机制

  中断只是一种协商机制，java 没有给中断增加任何语法，中断的过程完全需要程序员自己实现。

  若要中断一个线程， 你需要手动调用该方法的interrupt方法，该方法也仅仅是将线程对象的中断标识设成true

  按着你需要自己写代码不断地检测当前线程的标志位，如果为true，表示别的线程请求这条线程中断。

  此时究竟该做什么需要自己写代码实现



每个线程对象都有一个中断标识，用于表示线程是否被中断，该标识为true 表示中断， 为false 表示未中断

通过调用线程对象的interrupt 方法将该线程的标识位设为true 可以在别的线程中调用，也可以在自己的线程中调用。

###  如何停止中断运行中的线程

- 通过一个volatile 变量实现
- 通过AtomicBoolean 
- 通过Thread类自带的中断api实例方法实现

- 注意：sleep 方法抛出InterruptException后，中断标识也被清空为false，我们在catch没有通过调用th.interrupt() 方法再次将中断标识置为true，这就会导致无限循环

中断只是一种协商机制，并不会直接打断线程