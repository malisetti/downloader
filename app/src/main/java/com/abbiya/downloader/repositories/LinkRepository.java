package com.abbiya.downloader.repositories;

import com.abbiya.downloader.DownloaderApplication;
import com.abbiya.downloader.greendao.Link;
import com.abbiya.downloader.greendao.LinkDao;

import java.util.List;

/**
 * Created by seshachalam on 26/2/15.
 */
public class LinkRepository {

    public static long insertOrUpdate(Link link) {
        return getLinkDao().insertOrReplace(link);
    }

    public static void clearLinks() {
        getLinkDao().deleteAll();
    }

    public static void deleteLinkWithId(long id) {
        getLinkDao().delete(getLinkById(id));
    }

    public static Link getLinkById(long id) {
        return getLinkDao().load(id);
    }

    public static Link getLinkByHash(String hash){
        return getLinkDao().queryBuilder().where(LinkDao.Properties.Name.eq(hash)).list().get(0);
    }

    public static List<Link> getAllLinks() {
        return getLinkDao().loadAll();
    }

    private static LinkDao getLinkDao() {
        return DownloaderApplication.getInstance().getDaoSession().getLinkDao();
    }
}
