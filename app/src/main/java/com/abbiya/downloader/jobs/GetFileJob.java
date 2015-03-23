package com.abbiya.downloader.jobs;

import android.provider.MediaStore;
import android.webkit.URLUtil;

import com.abbiya.downloader.App;
import com.abbiya.downloader.Constants;
import com.abbiya.downloader.Utils;
import com.abbiya.downloader.events.DownloadFailedEvent;
import com.abbiya.downloader.events.DownloadLinkAddedEvent;
import com.abbiya.downloader.events.DownloadStartedEvent;
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
    String jobNum;

    public GetFileJob(String url, String part, String jobNum) {
        super(new Params(Priority.MID).requireNetwork().groupBy(Constants.GET_FILE));
        this.url = url;
        this.part = part;
        this.jobNum = jobNum;
    }

    @Override
    public void onAdded() {
        EventBus.getDefault().post(new DownloadLinkAddedEvent(url));
    }

    @Override
    public void onRun() throws Throwable {
        EventBus.getDefault().post(new DownloadStartedEvent());
        NetworkUtil networkUtil = new NetworkUtil();
        Response response = networkUtil.getFile(url, part);
        String fileName = URLUtil.guessFileName(url, "attachment", networkUtil.getRequest().header("Content-Type"));
        //File file = Utils.getTempFile(App.getInstance(), fileName);
        File targetDir = Utils.getDownloadStorageDir(Constants.DOWNLOADS_DIR);

        boolean directDownload = true;

        if (part.equals(Constants.NONE) || part.equals("")) {
            directDownload = false;
        } else {
            fileName = fileName + jobNum;
        }

        File file = new File(targetDir, fileName);
        InputStream in = response.body().byteStream();
        FileUtils.copyInputStreamToFile(in, file);

        EventBus.getDefault().post(new DownloadSuccessEvent(fileName, file.getPath(), url, part, directDownload));
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
