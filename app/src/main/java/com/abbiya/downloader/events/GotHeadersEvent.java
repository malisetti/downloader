package com.abbiya.downloader.events;

import com.squareup.okhttp.Headers;

/**
 * Created by seshachalam on 2/3/15.
 */
public class GotHeadersEvent {
    public String url;
    public Headers headers;

    public GotHeadersEvent(Headers headers, String url) {
        this.headers = headers;
        this.url = url;
    }
}
