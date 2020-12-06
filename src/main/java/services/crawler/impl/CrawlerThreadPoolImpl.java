package services.crawler.impl;

import java.util.concurrent.*;

public class CrawlerThreadPoolImpl {
    private ThreadPoolExecutor executor;
    private static int THREAD_POOL_SIZE = 200;
    private static CrawlerThreadPoolImpl instance;
    private CrawlerThreadPoolImpl() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static synchronized CrawlerThreadPoolImpl getInstance() {
        if (instance == null) {
            synchronized (CrawlerThreadPoolImpl.class) {
                if (instance == null) {
                    instance = new CrawlerThreadPoolImpl();
                }
            }
        }
        return instance;
    }

    public Future<String> submit(Callable<String> task) {
        Future<String> result = executor.submit(task);
        return result;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public void prestartAllCoreThreads() {
        executor.prestartAllCoreThreads();
    }
}
