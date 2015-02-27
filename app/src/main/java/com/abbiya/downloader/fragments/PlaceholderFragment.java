package com.abbiya.downloader.fragments;

/**
 * Created by seshachalam on 25/2/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbiya.downloader.DownloaderApplication;
import com.abbiya.downloader.R;
import com.abbiya.downloader.events.DownloadLinkAddedEvent;
import com.abbiya.downloader.events.DownloadProgressEvent;
import com.abbiya.downloader.jobs.DownloadFileJob;
import com.path.android.jobqueue.JobManager;

import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {

    JobManager jobManager;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button fetchBtn = (Button) rootView.findViewById(R.id.fetch);
        fetchBtn.setOnClickListener(this);

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
            jobManager.addJobInBackground(new DownloadFileJob(url));
            link.setText("");
            //Toast.makeText(getActivity(), "Link added to download list", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(DownloadLinkAddedEvent event) {
        //Toast.makeText(getActivity(), event.link.getDescription(), Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(DownloadProgressEvent event){
        Toast.makeText(getActivity(), " " + event.link.getProgress(), Toast.LENGTH_SHORT).show();
    }
}
