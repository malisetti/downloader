package com.abbiya.downloader.util;

import com.abbiya.downloader.DownloaderApplication;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by seshachalam on 5/3/15.
 */
public class NetworkUtil {

    private Request request;

    public Request getRequest(){
        return request;
    }

    public Response getHeaders(String url) throws IOException {
        request = new Request.Builder()
                .head()
                .url(url)
                .build();
        OkHttpClient client = DownloaderApplication.getInstance().getClient();
        Response response = client.newCall(request).execute();

        return response;
    }

    public Response getFile(String url, String part) throws IOException {
        request = new Request.Builder()
                .addHeader("Range", part)
                .get()
                .url(url)
                .build();
        OkHttpClient client = DownloaderApplication.getInstance().getClient();
        Response response = client.newCall(request).execute();

        return response;
    }

}
