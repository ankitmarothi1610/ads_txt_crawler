package data;

import models.Advertiser;

import java.util.List;

public interface AdvertiserDataService {
    void addAdvertiserBatch(List<Advertiser> advertiserList);
}
