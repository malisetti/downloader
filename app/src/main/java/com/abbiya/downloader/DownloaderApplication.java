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
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

public class DownloaderApplication extends Application {
    private static DownloaderApplication instance;
    public DaoSession daoSession;
    private JobManager jobManager;

    public DownloaderApplication() {
        instance = this;
    }

    public static DownloaderApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        generateDB();
        configureJobManager();
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
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

    public void generateDB() {
        DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(this,
                Constants.SQLITE_DB_NAME, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DaoMaster.createAllTables(db, true);
//        db.close();
//        dbHelper.close();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
