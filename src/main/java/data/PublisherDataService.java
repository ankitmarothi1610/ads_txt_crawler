package data;

import models.Publisher;

import java.util.List;

public interface PublisherDataService {
    int bulkUpdatePublishers(List<Publisher> publisherList);
}
