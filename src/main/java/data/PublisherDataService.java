package data;

import models.Publisher;

import java.sql.ResultSet;
import java.util.List;

public interface PublisherDataService {
    int bulkUpdatePublishers(List<Publisher> publisherList);
    ResultSet getIterablePublisherCrawlUrls();
    void markTrue(String url, String field);
}
