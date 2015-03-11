package com.abbiya.downloader;

/**
 * Created by seshachalam on 25/2/15.
 */
public final class Constants {
    public static final String SQLITE_DB_NAME = "abbiya.downloader";

    public static final String DOWNLOADS_DIR = "Downloader";
    //download status codes
    public static final int RANGE_UNSATISFIABLE = 416;
    public static final int PARTIAL_CONTENT = 206;
    public static final int OK = 200;

    //Job groups
    final public static String GET_HEADERS = "GET_HEADERS";
    final public static String GET_FILE = "GET_FILE";
    public static final String NONE = "none";
}
