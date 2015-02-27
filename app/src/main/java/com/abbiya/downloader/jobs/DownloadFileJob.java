package com.abbiya.downloader.jobs;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.abbiya.downloader.DownloaderApplication;
import com.abbiya.downloader.Utils;
import com.abbiya.downloader.events.DownloadLinkAddedEvent;
import com.abbiya.downloader.events.DownloadProgressEvent;
import com.abbiya.downloader.greendao.Link;
import com.abbiya.downloader.repositories.LinkRepository;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.greenrobot.event.EventBus;

/**
 * Created by seshachalam on 26/2/15.
 */
public class DownloadFileJob extends Job {

    private String url;
    private String urlHash;

    public DownloadFileJob(String url) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("downloads"));
        this.url = url;
    }

    @Override
    public void onRun() throws Throwable {
        Link link = LinkRepository.getLinkByHash(getMd5Hash(url));
        long downloadId = link.getDownloadId();
        boolean downloading = true;
        while (downloading) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadId);
            DownloadManager downloadManager = (DownloadManager) DownloaderApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = downloadManager.query(q);
            cursor.moveToFirst();

            int bytes_downloaded = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloading = false;
            }
            int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
            link.setProgress(dl_progress);
            LinkRepository.insertOrUpdate(link);
            EventBus.getDefault().post(new DownloadProgressEvent(link));
        }
    }

    @Override
    public void onAdded() {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) DownloaderApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);

        long did = downloadManager.enqueue(request);
        urlHash = getMd5Hash(url);

        Link link = new Link();
        link.setDownloadId(did);
        link.setDescription(url);
        link.setName(urlHash);
        LinkRepository.insertOrUpdate(link);

        EventBus.getDefault().post(new DownloadLinkAddedEvent(link));
    }

    private String getMd5Hash(String url){
        String urlHash = null;
        try{
            urlHash = Utils.md5(url);
        }catch (NoSuchAlgorithmException e){

        }

        return urlHash;
    }


    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    @Override
    protected void onCancel() {

    }
}
