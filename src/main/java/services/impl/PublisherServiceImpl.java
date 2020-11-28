package services.impl;

import com.google.common.base.Strings;
import data.PublisherDataService;
import data.impl.PublisherDataServiceImpl;
import models.Publisher;
import services.PublisherService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublisherServiceImpl implements PublisherService {
    private PublisherDataService publisherDataService;
    private static final int BATCH_SIZE = 500;

    public PublisherServiceImpl() {
        publisherDataService = new PublisherDataServiceImpl();
    }

    public int addPublishersFromFile(String filename) {
        int count = 0;
        File file  = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist " + file.getAbsoluteFile());
            return -1;
        }
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            List<Publisher> publisherList = new ArrayList<Publisher>(BATCH_SIZE);
            String line;
            while((line = br.readLine()) != null) {
                Publisher publisher = createPublisherObj(line);
                publisherList.add(publisher);
                if (publisherList.size() == BATCH_SIZE) {
                    count += publisherDataService.bulkUpdatePublishers(publisherList);
                    System.out.println("Processed a batch of " + publisherList.size() + " records");
                    publisherList.clear();
                }
            }
            if (publisherList.size() > 0) {
                count += publisherDataService.bulkUpdatePublishers(publisherList);
                System.out.println("Processed a batch of " + publisherList.size() + " records");
                publisherList.clear();
            }
        } catch(IOException io) {
            io.printStackTrace();
        }
        return count;
    }

    private String getNameFromUrl(String url) {
        return !Strings.isNullOrEmpty(url) ? url.substring(0, url.indexOf(".")) : null;
    }

    private Publisher createPublisherObj(String line) {
        line = line.strip();
        if (Strings.isNullOrEmpty(line))
            return null;
        Publisher publisher = new Publisher();
        publisher.url = line;
        publisher.name = getNameFromUrl(line);
        return publisher;
    }
}
