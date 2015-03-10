package com.abbiya.downloader.jobs;

import android.webkit.URLUtil;

import com.abbiya.downloader.Constants;
import com.abbiya.downloader.Utils;
import com.abbiya.downloader.events.DownloadFailedEvent;
import com.abbiya.downloader.events.DownloadSuccessEvent;
import com.abbiya.downloader.util.NetworkUtil;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.okhttp.Response;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by seshachalam on 5/3/15.
 */
public class GetFileJob extends Job {
    String url;
    String part;

    public GetFileJob(String url, String part) {
        super(new Params(Priority.MID).requireNetwork().groupBy(Constants.GET_FILE));
        this.url = url;
        this.part = part;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        NetworkUtil networkUtil = new NetworkUtil();
        Response response = networkUtil.getFile(url, part);

        InputStream in = response.body().byteStream();
        String fileName = URLUtil.guessFileName(url, "attachment", networkUtil.getRequest().header("Content-Type"));
        File targetDir = Utils.getDownloadStorageDir(Constants.DOWNLOADS_DIR);

        boolean directDwnld = true;

        if(part.equals("none") || part.equals("")){
            directDwnld = false;
        }else{
            fileName = fileName + part;
        }

        File file = new File(targetDir, fileName);

        FileUtils.copyInputStreamToFile(in, file);

        EventBus.getDefault().post(new DownloadSuccessEvent(fileName, targetDir.getPath(), url, part, directDwnld));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        EventBus.getDefault().post(new DownloadFailedEvent(throwable.getMessage()));
        return false;
    }
}
