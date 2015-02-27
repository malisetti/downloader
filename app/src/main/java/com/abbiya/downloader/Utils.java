package com.abbiya.downloader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by seshachalam on 26/2/15.
 */
public class Utils {

    public static synchronized String md5(String input) throws NoSuchAlgorithmException{
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes();
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return hexStr;
    }
}
