package com.abbiya.downloader.events;

import com.abbiya.downloader.greendao.Link;

/**
 * Created by seshachalam on 26/2/15.
 */
public class DownloadProgressEvent {
    public Link link;

    public DownloadProgressEvent(Link link){
        this.link = link;
    }
}
