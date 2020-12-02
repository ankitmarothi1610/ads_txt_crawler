package services.crawler.impl;

import data.PublisherDataService;
import data.impl.PublisherDataServiceImpl;
import services.CrawlerImpl;

import java.util.concurrent.Callable;

public class CrawlerThreadImpl implements Callable<String> {
    String url;
    int threadid;
    CrawlerImpl crawler;
    PublisherDataService publisherDataService;
    public CrawlerThreadImpl(String url, int id) {
        this.url = url;
        this.crawler = new CrawlerImpl();
        this.threadid = id;
        this.publisherDataService = new PublisherDataServiceImpl();
    }

    public int getName() {
        return this.threadid;
    }

    @Override
    public String call() throws Exception {
        String localFilePath = crawler.downloadFile(url);
        crawler.sourceFile(localFilePath);
        publisherDataService.markProcessed(url);
        return url;
    }
}
