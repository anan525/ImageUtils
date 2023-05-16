package com.imageloader.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

    public static String getMd5(String key) {
        String md5String = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = key.getBytes();
            byte[] digest = md5.digest(bytes);
            md5String = bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5String;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toLowerCase();
    }
}
