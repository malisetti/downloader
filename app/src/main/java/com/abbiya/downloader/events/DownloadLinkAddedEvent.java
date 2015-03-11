package com.abbiya.downloader.events;

/**
 * Created by seshachalam on 26/2/15.
 */
public class DownloadLinkAddedEvent {
    public String link;

    public DownloadLinkAddedEvent(String link) {
        this.link = link;
    }
}
