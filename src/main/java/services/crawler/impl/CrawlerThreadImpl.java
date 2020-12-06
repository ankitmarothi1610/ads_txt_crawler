package services.crawler.impl;

import data.AdvertiserDataService;
import data.PublisherDataService;
import data.impl.AdvertiserDataServiceImpl;
import data.impl.PublisherDataServiceImpl;
import models.Publisher;

import java.util.concurrent.Callable;

public class CrawlerThreadImpl implements Callable<String> {
    Publisher publisher;
    int threadid;
    CrawlerImpl crawler;
    PublisherDataService publisherDataService;
    AdvertiserDataService advertiserDataService;
    public CrawlerThreadImpl(Publisher publisher, int id) {
        this.publisher = publisher;
        this.crawler = new CrawlerImpl();
        this.threadid = id;
        this.publisherDataService = new PublisherDataServiceImpl();
        this.advertiserDataService = new AdvertiserDataServiceImpl();
    }

    public int getName() {
        return this.threadid;
    }

    @Override
    public String call() throws Exception {
        System.out.println("Downloading file for thread " + threadid + " url " + publisher.url);
        String localFilePath = crawler.downloadFile(publisher.url);
        if (localFilePath != null) {
            System.out.println("Deleting previous publisher data " + threadid + " url " + publisher.url);
            advertiserDataService.deleteAdsDataForPublisher(publisher);
            System.out.println("Sourcing file for thread " + threadid + " url " + publisher.url);
            crawler.sourceFile(publisher, localFilePath);
            System.out.println("Marking processed for thread " + threadid + " url " + publisher.url);
            publisherDataService.markTrue(publisher.url, "processed");
        } else {
            System.out.println("Marking not Found for thread " + threadid + " url " + publisher.url);
            publisherDataService.markTrue(publisher.url, "notFound");
        }
        return publisher.url;
    }
}
