package services.publisher.impl;

import com.google.common.base.Strings;
import helpers.PublisherHelper;
import models.Publisher;
import services.publisher.PublisherService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PublisherServiceImpl implements PublisherService {
    private static final int BATCH_SIZE = 1000;
    private static final int THREAD_POOL_SIZE = 100;
    private static final int QUEUE_SIZE = 1000;
    PublisherThreadPoolImpl publisherThreadPool;

    public PublisherServiceImpl() {
        printWarmUpDetails();
        publisherThreadPool = PublisherThreadPoolImpl.getInstance();
        setThreadPoolRejectionHandler();
    }

    public void printWarmUpDetails() {
        System.out.println("Intializing the publisher thread pool with size " + THREAD_POOL_SIZE);
        System.out.println("MAX size of the thread pool is " + THREAD_POOL_SIZE);
        System.out.println("Blocking Queue size is " + QUEUE_SIZE);
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
        String line;
        try {
            int i = 0;
            List<Publisher> publisherList = new ArrayList<Publisher>(BATCH_SIZE);
            List<Future<Integer>> futureList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                Publisher publisher = createPublisherObj(line);
                if (publisher != null) {
                    publisherList.add(publisher);
                    i++;
                }
                if (i == BATCH_SIZE) {
                    Future<Integer> result = PublisherThreadPoolImpl
                            .getInstance()
                            .submit(new PublisherThreadImpl(publisherList, threadCount));
                    futureList.add(result);
                    threadCount++;
                    i = 0;
                    publisherList = new ArrayList<Publisher>(BATCH_SIZE);
                }
            }
            if (publisherList.size() > 0) {
                Future<Integer> result = PublisherThreadPoolImpl
                        .getInstance()
                        .submit(new PublisherThreadImpl(publisherList, threadCount));
                futureList.add(result);
                publisherList.clear();
            }
            for(Future<Integer> future : futureList) {
                try {
                    System.out.println("Future result is - " + " - " + future.get() + "; And Task done is " + future.isDone());
                }
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception io) {
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
