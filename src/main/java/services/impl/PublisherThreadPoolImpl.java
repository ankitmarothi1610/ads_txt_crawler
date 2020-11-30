package services.impl;

import java.util.concurrent.*;

public class PublisherThreadPoolImpl {
    public static ThreadPoolExecutor executor;
//    public PublisherThreadPoolImpl(int corePoolSize, int maximumPoolSize,
//                                   long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
//        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
//    }

    public PublisherThreadPoolImpl(int maximumPoolSize) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maximumPoolSize);
    }

    public void submit(Runnable task) {
        executor.submit(task);
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public void prestartAllCoreThreads() {
        executor.prestartAllCoreThreads();
    }
}
