package data;

import models.Advertiser;
import models.Publisher;

import java.util.List;

public interface AdvertiserDataService {
    void addAdvertiserBatch(List<Advertiser> advertiserList);
    void deleteAdsDataForPublisher(Publisher publisher);
}
