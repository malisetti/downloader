package com.abbiya.downloader.fragments;

/**
 * Created by seshachalam on 25/2/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abbiya.downloader.Constants;
import com.abbiya.downloader.DownloaderApplication;
import com.abbiya.downloader.R;
import com.abbiya.downloader.Utils;
import com.abbiya.downloader.events.DownloadFailedEvent;
import com.abbiya.downloader.events.DownloadSuccessEvent;
import com.abbiya.downloader.events.GotHeadersEvent;
import com.abbiya.downloader.jobs.GetFileJob;
import com.abbiya.downloader.jobs.GetHeadersJob;
import com.path.android.jobqueue.AsyncAddCallback;
import com.path.android.jobqueue.JobManager;
import com.squareup.okhttp.Headers;

import org.apache.commons.io.FileUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {

    static final Executor NETWORK_EXECUTOR = Executors.newCachedThreadPool();
    static final Executor DISK_EXECUTOR = Executors.newCachedThreadPool();

    JobManager jobManager;
    TextView status;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button fetchBtn = (Button) rootView.findViewById(R.id.fetch);
        fetchBtn.setOnClickListener(this);

        status = (TextView) rootView.findViewById(R.id.status);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobManager = DownloaderApplication.getInstance().getJobManager();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            //this may crash if registration did not go through. just be safe
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fetch:
                onFetchBtnClicked(view);
                break;
            default:
                break;
        }
    }

    private void onFetchBtnClicked(View view) {
        EditText link = (EditText) getActivity().findViewById(R.id.link);

        String url = link.getText().toString();

        if (url != null && url.trim().length() > 0) {
            if(!Utils.isExternalStorageReadable() || !Utils.isExternalStorageWritable()){
                Toast.makeText(getActivity(), "File system error", Toast.LENGTH_LONG).show();
                return;
            }
            jobManager.addJobInBackground(new GetHeadersJob(url), new AsyncAddCallback() {
                @Override
                public void onAdded(long jobId) {

                }
            });
            jobManager.clear();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GotHeadersEvent event) {
        final Headers headers = event.headers;
        String contentType = headers.get("Content-Type");
        String contentLength = headers.get("Content-Length");

        String ext = "file";

        if (contentType != null) {
            ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);
        }

        long cLength = 0;
        if (contentLength != null) {
            cLength = Long.valueOf(contentLength).longValue();
        }

        long freeSpace = Utils.getFreeSpace(Constants.DOWNLOADS_DIR);
        if(freeSpace <= (cLength + 5000000)){
            Toast.makeText(getActivity(), "Seems not much space is available", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isResumable = acceptsMultipartDownloads(headers);

        if(isResumable){
            long i = 0;
            long mb = FileUtils.ONE_MB;
            while (i<=cLength){
                if(cLength - i <= mb){
                    /*
                    jobManager.addJobInBackground(new GetFileJob(event.url, "bytes=" + String.valueOf(i) + "-" + String.valueOf(cLength)), new AsyncAddCallback() {
                        @Override
                        public void onAdded(long jobId) {

                        }
                    });
                    */
                }else{
                    /*
                    jobManager.addJobInBackground(new GetFileJob(event.url, "bytes=" + String.valueOf(i) + "-" + String.valueOf(i + mb)), new AsyncAddCallback() {
                        @Override
                        public void onAdded(long jobId) {

                        }
                    });
                    */
                }
                i = i + mb;
            }
        }else{

        }

        final GetFileJob getFileJob = new GetFileJob(event.url, "none");
        jobManager.addJobInBackground(getFileJob, new AsyncAddCallback() {
            @Override
            public void onAdded(long jobId) {

            }
        });
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(DownloadSuccessEvent event) {
        Toast.makeText(getActivity(), event.fileName, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(DownloadFailedEvent event) {
        Toast.makeText(getActivity(), event.errorMessage, Toast.LENGTH_LONG).show();
    }

    private boolean acceptsMultipartDownloads(Headers headers) {
        String acceptRanges = headers.get("Accept-Ranges");
        if (acceptRanges == null || acceptRanges.equals("none")) {
            return false;
        }
        return true;
    }

}
