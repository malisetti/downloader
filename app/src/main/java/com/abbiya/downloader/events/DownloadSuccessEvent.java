package com.abbiya.downloader.events;

/**
 * Created by seshachalam on 26/2/15.
 */
public class DownloadSuccessEvent {
    public String fileName;
    public String dir;
    public String url;
    public String part;
    public boolean isPart;

    public DownloadSuccessEvent(String fileName, String dir, String url, String part, boolean isPart){
        this.fileName = fileName;
        this.dir = dir;
        this.url = url;
        this.part = part;
        this.isPart = isPart;
    }
}
