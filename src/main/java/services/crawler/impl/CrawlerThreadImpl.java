package services.crawler.impl;

import data.AdvertiserDataService;
import data.PublisherDataService;
import data.impl.AdvertiserDataServiceImpl;
import data.impl.PublisherDataServiceImpl;
import models.Publisher;

import java.util.concurrent.Callable;

public class CrawlerThreadImpl implements Callable<String> {
    Publisher publisher;
    CrawlerImpl crawler;
    PublisherDataService publisherDataService;
    AdvertiserDataService advertiserDataService;
    public CrawlerThreadImpl(Publisher publisher) {
        this.publisher = publisher;
        this.crawler = new CrawlerImpl();
        this.publisherDataService = new PublisherDataServiceImpl();
        this.advertiserDataService = new AdvertiserDataServiceImpl();
    }

    @Override
    public String call() throws Exception {
        System.out.println("Downloading file for url " + publisher.url);
        String localFilePath = crawler.downloadFile(publisher.url);
        if (localFilePath != null) {
            System.out.println("Deleting previous publisher data url " + publisher.url);
            advertiserDataService.deleteAdsDataForPublisher(publisher);
            System.out.println("Sourcing file for thread url " + publisher.url);
            crawler.sourceFile(publisher, localFilePath);
            System.out.println("Marking processed for thread url " + publisher.url);
            publisherDataService.markTrue(publisher.url, "processed");
        } else {
            System.out.println("Marking not Found for thread url " + publisher.url);
            publisherDataService.markTrue(publisher.url, "notFound");
        }
        return publisher.url;
    }
}
