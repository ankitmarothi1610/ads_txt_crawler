package services.crawler.impl;

import services.CrawlerImpl;

import java.util.concurrent.Callable;

public class CrawlerThreadImpl implements Callable<String> {
    String url;
    int threadid;
    CrawlerImpl crawler;
    public CrawlerThreadImpl(String url, int id) {
        this.url = url;
        this.crawler = new CrawlerImpl();
        this.threadid = id;
    }

    public int getName() {
        return this.threadid;
    }

    @Override
    public String call() throws Exception {
        String localFilePath = crawler.downloadFile(url);
        crawler.sourceFile(localFilePath);
        return url;
    }
}
