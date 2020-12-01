package services.impl;

import java.util.concurrent.*;

public class PublisherThreadPoolImpl {
    public static ThreadPoolExecutor executor;
//    public PublisherThreadPoolImpl(int corePoolSize, int maximumPoolSize,
//                                   long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
//        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
//    }

    public PublisherThreadPoolImpl(int maximumPoolSize) {
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public Future<Integer> submit(Callable<Integer> task) {
        Future<Integer> result = executor.submit(task);
        return result;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public void prestartAllCoreThreads() {
        executor.prestartAllCoreThreads();
    }
}
