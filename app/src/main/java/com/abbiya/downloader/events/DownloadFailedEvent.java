package com.abbiya.downloader.events;

/**
 * Created by seshachalam on 26/2/15.
 */
public class DownloadFailedEvent {
    public String errorMessage;

    public DownloadFailedEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
