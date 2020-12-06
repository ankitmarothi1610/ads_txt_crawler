package helpers;

import com.google.common.base.Strings;

public class PublisherHelper {
    public static int FETCH_SIZE = 1000;

    public static String getNameFromUrl(String url) {
        return !Strings.isNullOrEmpty(url) ? url.substring(0, url.indexOf(".")) : null;
    }
}
