package services.impl;

import com.google.common.base.Strings;
import data.PublisherDataService;
import data.impl.PublisherDataServiceImpl;
import helpers.PublisherHelper;
import models.Publisher;
import services.PublisherService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PublisherServiceImpl implements PublisherService {
    private PublisherDataService publisherDataService;
    private static final int BATCH_SIZE = 50;
    private static final int THREAD_POOL_SIZE = 50;
    private static final int QUEUE_SIZE = 50;
    BlockingQueue<Runnable> blockingQueue;
    PublisherThreadPoolImpl publisherThreadPool;

    public PublisherServiceImpl() {
        publisherDataService = new PublisherDataServiceImpl();
        blockingQueue =  new ArrayBlockingQueue<Runnable>(QUEUE_SIZE);
        printWarmUpDetails();
        publisherThreadPool = createThreadPool();
        setThreadPoolRejectionHandler();
    }

    public void printWarmUpDetails() {
        System.out.println("Intializing the publisher thread pool with size " + THREAD_POOL_SIZE);
        System.out.println("MAX size of the thread pool is " + THREAD_POOL_SIZE);
        System.out.println("Blocking Queue size is " + QUEUE_SIZE);
    }

    private PublisherThreadPoolImpl createThreadPool() {
        return new PublisherThreadPoolImpl(
                THREAD_POOL_SIZE,
                THREAD_POOL_SIZE,
                60,
                TimeUnit.SECONDS,
                blockingQueue
        );
    }

    private void setThreadPoolRejectionHandler() {
        publisherThreadPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r,
                                          ThreadPoolExecutor executor) {
                System.out.println("PublisherThreadTask Rejected : "
                        + ((PublisherThreadImpl) r).getName());
                System.out.println("Waiting for a second !!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread added once again time : "
                        + ((PublisherThreadImpl) r).getName());
                executor.execute(r);
            }
        });
    }

    public void addPublishersFromFile(String filename) {
        int count = 0;
        BufferedReader br = null;
        FileReader fr = null;
        File file  = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist " + file.getAbsoluteFile());
            return;
        }
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            publisherThreadPool.prestartAllCoreThreads();
            processFile(br);
        } catch(IOException io) {
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    private void processFile(BufferedReader br) {
        int threadCount = 0;
        List<Publisher> publisherList = new ArrayList<Publisher>(BATCH_SIZE);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                Publisher publisher = createPublisherObj(line);
                publisherList.add(publisher);
                if (publisherList.size() == BATCH_SIZE) {
                    publisherThreadPool.execute(new PublisherThreadImpl(publisherList, threadCount + ""));
                    System.out.println("Processed a batch of " + publisherList.size() + " records");
                    publisherList.clear();
                    threadCount++;
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException ie) {
//                        ie.printStackTrace();
//                    }
                }
            }
            if (publisherList.size() > 0) {
                new PublisherThreadImpl(publisherList, threadCount + "");
                publisherDataService.bulkUpdatePublishers(publisherList);
                publisherThreadPool.execute(new PublisherThreadImpl(publisherList, threadCount + ""));
                System.out.println("Processed a batch of " + publisherList.size() + " records");
                publisherList.clear();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private Publisher createPublisherObj(String line) {
        line = line.strip();
        if (Strings.isNullOrEmpty(line))
            return null;
        Publisher publisher = new Publisher();
        publisher.url = line;
        publisher.name = PublisherHelper.getNameFromUrl(line);
        return publisher;
    }
}
