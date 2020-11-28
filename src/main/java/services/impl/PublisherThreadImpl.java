package services.impl;

import data.PublisherDataService;
import data.impl.PublisherDataServiceImpl;
import models.Publisher;

import java.util.List;

public class PublisherThreadImpl implements Runnable {
    String name;
    List<Publisher> publisherList;
    PublisherDataService publisherDataService;

    public PublisherThreadImpl(List<Publisher> publisherList, String name) {
        publisherDataService = new PublisherDataServiceImpl();
        this.publisherList = publisherList;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public void run() {
        System.out.println(name + ": Processing a batch of " + publisherList.size() + " records");
        publisherDataService.bulkUpdatePublishers(publisherList);
        System.out.println(name + "Processed a batch of " + publisherList.size() + " records");
    }
}
