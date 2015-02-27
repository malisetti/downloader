package com.abbiya.downloader.events;

import com.abbiya.downloader.greendao.Link;

/**
 * Created by seshachalam on 26/2/15.
 */
public class DownloadLinkAddedEvent {
    public Link link;

    public DownloadLinkAddedEvent(Link link) {
        this.link = link;
    }
}
