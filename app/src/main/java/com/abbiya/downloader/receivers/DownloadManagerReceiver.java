package com.abbiya.downloader.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abbiya.downloader.events.DownloadManagerEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by seshachalam on 25/2/15.
 */
public class DownloadManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new DownloadManagerEvent());
    }
}
