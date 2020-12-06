package services.crawler.impl;

import data.AdvertiserDataService;
import data.PublisherDataService;
import data.impl.AdvertiserDataServiceImpl;
import data.impl.PublisherDataServiceImpl;
import db.MysqlClientManager;
import helpers.AdvertiserHelper;
import models.Publisher;
import services.crawler.AdvertiserService;
import services.publisher.impl.PublisherThreadImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
        ResultSet rs = publisherDataService.getIterablePublisherCrawlUrls();
        int i = 0;
        List<Publisher> publishersList = new ArrayList<>(0);
        try {
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.id = rs.getInt(1);
                publisher.name = rs.getString(2);
                publisher.url = rs.getString(3);
                publishersList.add(publisher);
                i++;
                if (i == AdvertiserHelper.FETCH_SIZE) {
                    crawlPublisherUrls(publishersList);
                    i = 0;
                    publishersList.clear();
                }
            }
            if (i > 0) {
                crawlPublisherUrls(publishersList);
                publishersList.clear();
            }
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(null, rs);
            crawlerThreadPool.awaitShutDown();
        }
    }

    public void crawlPublisherUrls(List<Publisher> publisherList) {
        List<Future<String>> futureList = new ArrayList<>();
        int threadCount = 1;
        for (Publisher publisher: publisherList) {
            System.out.println("Submitting processing for url " + publisher.url);
            Future<String> result = CrawlerThreadPoolImpl
                    .getInstance()
                    .submit(new CrawlerThreadImpl(publisher, threadCount));
            futureList.add(result);
            threadCount++;
        }
    }
}
