package data;

import models.Publisher;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface PublisherDataService {
    int bulkUpdatePublishers(List<Publisher> publisherList);
    ResultSet getIterablePublisherCrawlUrls();
    void markTrue(String url, String field);
    void setFlagsToFalse();
    Map<String, Integer> getMaxAndMinIds();
    List<Publisher> getRecordsBetweenIds(int min, int max);
}
