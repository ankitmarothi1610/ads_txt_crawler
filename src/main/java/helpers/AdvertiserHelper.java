package helpers;

import com.google.common.base.Strings;
import models.Advertiser;

public class AdvertiserHelper {
    public static int FETCH_SIZE = 200;

    public static Advertiser createAdvertiserObject(int publisherId, String line) {
        String tokens[] = line.split(",");
        Advertiser advertiser = new Advertiser();
        if (tokens.length < 3) {
            return null;
        }
        if (!Strings.isNullOrEmpty(tokens[0])) {
            if (!Strings.isNullOrEmpty(tokens[0].strip()))
                advertiser.name = tokens[0].strip();
            if (!Strings.isNullOrEmpty(tokens[1].strip()))
                advertiser.advertiserId = tokens[1].strip();
            if (!Strings.isNullOrEmpty(tokens[2].strip()))
                advertiser.accountType = tokens[2].strip();
            if (tokens.length >= 4 && !Strings.isNullOrEmpty(tokens[3].strip()))
                advertiser.tag = tokens[3].strip();
            advertiser.publisherId = publisherId;
        }
        return advertiser;
    }

}
