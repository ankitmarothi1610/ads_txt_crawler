package services;

import com.google.common.base.Strings;
import data.AdvertiserDataService;
import data.impl.AdvertiserDataServiceImpl;
import helpers.AdvertiserHelper;
import models.Advertiser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class CrawlerImpl {
    public static String FILE_DIR = "src/main/resources/ads_txt/";
    public static String HTTP_PREFIX = "http://";
    public static String HTTPS_PREFIX = "https://";
    public static String ADS_FILE = "ads.txt";
    private final AdvertiserDataService advertiserDataService;

    public CrawlerImpl() {
        advertiserDataService = new AdvertiserDataServiceImpl();
    }

    public String downloadFile(String url) {
        String fileName = url.replace(".", "_");
        String filePath = FILE_DIR + "" + fileName;
        BufferedInputStream inputStream = null;
        FileOutputStream fileOS = null;
        String fullUrl = getFullUrl(url);
        if (!Strings.isNullOrEmpty(fullUrl)) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =
                        (HttpURLConnection) new URL(fullUrl).openConnection();
                inputStream = new BufferedInputStream(con.getInputStream());
                fileOS = new FileOutputStream(filePath);
                byte[] data = new byte[1024];
                int byteContent;
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    fileOS.write(data, 0, byteContent);
                }
            } catch (IOException ie) {
                ie.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                    if (fileOS != null)
                        fileOS.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        } else {
            System.out.println(fullUrl + " for " + url + " Not found");
            return null;
        }
        return filePath;
    }

    public void sourceFile(String filePath) {
        File file = new File(filePath);
        FileReader fr;
        BufferedReader br;
        String line;
        List<Advertiser> advertiserList;
        if (!file.exists()) {
            System.out.println("File " + filePath + " does not exist");
            return;
        }
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            advertiserList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                line = line.strip();
                if (line.startsWith("#")) {
                    continue;
                }
                Advertiser advertiser = AdvertiserHelper.createAdvertiserObject(line);
                int i = 0;
                if (advertiser != null) {
                    advertiserList.add(advertiser);
                    i++;
                    if (i % AdvertiserHelper.FETCH_SIZE == 0) {
                        advertiserDataService.addAdvertiserBatch(advertiserList);
                        System.out.println("Added a batch of " + advertiserList.size() + " records");
                        advertiserList.clear();
                    }
                }
            }
            if (advertiserList.size() > 0) {
                advertiserDataService.addAdvertiserBatch(advertiserList);
                System.out.println("Added a batch of " + advertiserList.size() + " records");
                advertiserList.clear();
            }
        } catch(IOException ie) {
            ie.printStackTrace();
        }
    }

    public String getFullUrl(String url) {
        String httpUrl = HTTP_PREFIX + url + "/" + ADS_FILE;
        String httpsUrl = HTTPS_PREFIX + url + "/" + ADS_FILE;
        return (checkIfUrlExists(httpUrl) ? httpUrl : (checkIfUrlExists(httpsUrl) ? httpsUrl : null));
    }

    public boolean checkIfUrlExists(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
