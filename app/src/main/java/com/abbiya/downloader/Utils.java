package com.abbiya.downloader;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by seshachalam on 26/2/15.
 */
public class Utils {

    public synchronized static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return hexStr;
    }

    public synchronized static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getDownloadStorageDir(String dir) throws IOException {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static long getFreeSpace(String dir){
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), dir);

        return file.getFreeSpace();
    }

    public static File merge(File one, File two){
        File mergedFile = null;
        try {
             mergedFile = getTempFile(DownloaderApplication.getInstance().getApplicationContext(), "fd");
            FileInputStream fis1 = new FileInputStream(one);
            FileInputStream fis2 = new FileInputStream(two);
            SequenceInputStream sis = new SequenceInputStream(fis1,fis2);

            if(!mergedFile.exists()){
                mergedFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(mergedFile);

            int temp;

            while ((temp = sis.read())!= -1){
                fos.write((byte)temp);
            }

            fis1.close();
            fis2.close();
            sis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergedFile;
    }

    public static File getTempFile(Context context, String fileName) throws IOException {
        File file = null;
        //String fileName = Uri.parse(url).getLastPathSegment();
        file = File.createTempFile(fileName, null, context.getCacheDir());
        return file;
    }
}
