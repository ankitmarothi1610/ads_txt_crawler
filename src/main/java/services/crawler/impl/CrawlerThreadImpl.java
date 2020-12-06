package services.crawler.impl;

import data.PublisherDataService;
import data.impl.PublisherDataServiceImpl;

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
        System.out.println("Downloading file for thread " + threadid + " url " + url);
        String localFilePath = crawler.downloadFile(url);
        if (localFilePath != null) {
            System.out.println("Sourcing file for thread " + threadid + " url " + url);
            crawler.sourceFile(localFilePath);
            System.out.println("Marking processed for thread " + threadid + " url " + url);
            publisherDataService.markTrue(url, "processed");
        } else {
            System.out.println("Marking not Found for thread " + threadid + " url " + url);
            publisherDataService.markTrue(url, "notFound");
        }
        return url;
    }
}
