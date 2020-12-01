package services.impl;

import data.PublisherDataService;
import data.impl.PublisherDataServiceImpl;
import models.Publisher;

import java.util.List;
import java.util.concurrent.Callable;

public class PublisherCallableImpl implements Callable<Integer> {
    List<Publisher> publisherList;
    PublisherDataService publisherDataService;
    int threadid;
    public PublisherCallableImpl(List<Publisher> publisherList, int id) {
        this.publisherList = publisherList;
        this.publisherDataService = new PublisherDataServiceImpl();
        this.threadid = id;
    }

    public int getName() {
        return this.threadid;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Processing a batch of " + publisherList.size() + " records");
        publisherDataService.bulkUpdatePublishers(publisherList);
        System.out.println("Processed a batch of " + publisherList.size() + " records");
        return publisherList.size();
    }
}
