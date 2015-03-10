package com.abbiya.downloader.jobs;

import com.abbiya.downloader.Constants;
import com.abbiya.downloader.events.GotHeadersEvent;
import com.abbiya.downloader.util.NetworkUtil;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.okhttp.Response;

import de.greenrobot.event.EventBus;

/**
 * Created by seshachalam on 5/3/15.
 */
public class GetHeadersJob extends Job {

    private String url;

    public GetHeadersJob(String url) {
        super(new Params(Priority.HIGH).requireNetwork().groupBy(Constants.GET_HEADERS));
        this.url = url;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        NetworkUtil networkUtil = new NetworkUtil();
        Response response = networkUtil.getHeaders(url);

        EventBus.getDefault().post(new GotHeadersEvent(response.headers(), url));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
