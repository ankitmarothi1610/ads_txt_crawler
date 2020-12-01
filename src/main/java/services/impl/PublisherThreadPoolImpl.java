package services.impl;

import java.util.concurrent.*;

public class PublisherThreadPoolImpl {
    private ThreadPoolExecutor executor;
    private static int THREAD_POOL_SIZE = 100;
    private static PublisherThreadPoolImpl instance;
    private PublisherThreadPoolImpl() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static synchronized PublisherThreadPoolImpl getThreadPool() {
        if (instance == null) {
            synchronized (PublisherThreadPoolImpl.class) {
                if (instance == null) {
                    instance = new PublisherThreadPoolImpl();
                }
            }
        }
        return instance;
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
