package com.abbiya.downloader;

/**
 * Created by seshachalam on 25/2/15.
 */

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.abbiya.downloader.greendao.DaoMaster;
import com.abbiya.downloader.greendao.DaoMaster.DevOpenHelper;
import com.abbiya.downloader.greendao.DaoSession;
import com.abbiya.downloader.util.PingNetworkUtil;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.squareup.okhttp.OkHttpClient;

public class DownloaderApplication extends Application {
    private static DownloaderApplication instance;
    private static OkHttpClient client;
    public DaoSession daoSession;
    private JobManager jobManager;

    public DownloaderApplication() {
        instance = this;
    }

    public synchronized static DownloaderApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        generateDB();
        configureJobManager();
    }

    @Override
    public void onTerminate() {
        try {
            daoSession.clear();
        } catch (Exception e) {

        }
        super.onTerminate();
    }

    private void generateDB() {
        DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(this,
                Constants.SQLITE_DB_NAME, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DaoMaster.createAllTables(db, true);
//        db.close();
//        dbHelper.close();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .networkUtil(new PingNetworkUtil(instance))
                .minConsumerCount(3)//always keep at least one consumer alive
                .maxConsumerCount(5)//up to 3 consumers at a time
                .loadFactor(1)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public synchronized OkHttpClient getClient() {
        return client == null ? new OkHttpClient() : client;
    }
}
