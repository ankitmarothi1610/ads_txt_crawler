package services.crawler.impl;

import data.AdvertiserDataService;
import data.PublisherDataService;
import data.impl.AdvertiserDataServiceImpl;
import data.impl.PublisherDataServiceImpl;
import models.Publisher;
import services.crawler.AdvertiserService;
import services.publisher.impl.PublisherThreadImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class AdvertiserServiceImpl implements AdvertiserService {
    AdvertiserDataService advertiserDataService;
    PublisherDataService publisherDataService;
    CrawlerThreadPoolImpl crawlerThreadPool;

    public AdvertiserServiceImpl() {
        advertiserDataService = new AdvertiserDataServiceImpl();
        publisherDataService = new PublisherDataServiceImpl();
        crawlerThreadPool = CrawlerThreadPoolImpl.getInstance();
        setThreadPoolRejectionHandler();
    }

    private void setThreadPoolRejectionHandler() {
        crawlerThreadPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r,
                                          ThreadPoolExecutor executor) {
                System.out.println("CrawlerThreadTask Rejected : "
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

    @Override
    public void sourceAdsTxtForPublisherUrls() {
        publisherDataService.setFlagsToFalse();
//        ResultSet rs = publisherDataService.getIterablePublisherCrawlUrls();
        Map<String, Integer> ids = publisherDataService.getMaxAndMinIds();
        int max = ids.get("max");
        int min = ids.get("min");
        int minId = min;
        int maxId = min;
        while (minId < max) {
            maxId += minId + 100;
            if (maxId > max)
                maxId = max;
            sourceBatch(minId, maxId);
            minId = maxId;
        }
    }

    public void sourceBatch(int max, int min) {
        List<Publisher> publisherList = publisherDataService.getRecordsBetweenIds(max, min);
        for(Publisher publisher: publisherList) {
            crawlPublisherUrls(publisher);
        }
    }

    public void crawlPublisherUrls(Publisher publisher) {
        System.out.println("Submitting processing for url " + publisher.url);
        Future<String> result = CrawlerThreadPoolImpl
                .getInstance()
                .submit(new CrawlerThreadImpl(publisher));
        try {
            System.out.println("Future is " + result.isDone() + " and result is " + result.get());
        } catch (InterruptedException | ExecutionException ie) {
            ie.printStackTrace();
        }
    }
}
