package services.crawler.impl;

import data.AdvertiserDataService;
import data.PublisherDataService;
import data.impl.AdvertiserDataServiceImpl;
import data.impl.PublisherDataServiceImpl;
import db.MysqlClientManager;
import helpers.AdvertiserHelper;
import services.CrawlerImpl;
import services.crawler.AdvertiserService;
import services.publisher.impl.PublisherThreadImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        ResultSet rs = publisherDataService.getIterablePublisherCrawlUrls();
        int i = 1;
        List<String> urls = new ArrayList<>(0);
        try {
            while (rs.next()) {
                urls.add(rs.getString(1));
                i++;
                if (i == AdvertiserHelper.FETCH_SIZE) {
                    crawlPublisherUrls(urls);
                    publisherDataService.markProcessed(urls);
                    i = 1;
                }
            }
            if (i > 0) {
                crawlPublisherUrls(urls);
                publisherDataService.markProcessed(urls);
            }
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            MysqlClientManager.destroyQueryObjects(null, rs);
        }
    }

    public void crawlPublisherUrls(List<String> urls) {
        CrawlerImpl crawlerImpl = new CrawlerImpl();
        List<Future<String>> futureList = new ArrayList<>();
        int threadCount = 1;
        for (String url: urls) {
            crawlerImpl.downloadFile(url);
            Future<String> result = CrawlerThreadPoolImpl
                    .getInstance()
                    .submit(new CrawlerThreadImpl(url, threadCount));
            futureList.add(result);
            threadCount++;
        }
        for(Future<String> future : futureList) {
            try {
                System.out.println("Future result is - " + " - " + future.get() + "; And Task done is " + future.isDone());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
