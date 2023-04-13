package com.hmdp.utils;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.MD5;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;

public class PasswordUtil {

    /**
     * 散列次数
     */
    private static final int DIGEST_COUNT = 10;

    /**
     * 随机盐值字节数
     */
    private static final int SALT_BYTE_LENGTH = 20;

    /**
     * 编码
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * 密码加密
     * @param password 密码
     * @return
     */
    public static String encrypt(String password){
        SecureRandom secureRandom = new SecureRandom();
        byte salt[] = new byte[SALT_BYTE_LENGTH];
        secureRandom.nextBytes(salt);
        MD5 md5 = new MD5(salt, DIGEST_COUNT);
        byte[] digest = md5.digest(password,CHARSET);
        //填充前20个字节为盐值，校验密码时候需要取出
        byte[] pwd = ArrayUtil.addAll(salt,digest);
        return HexUtil.encodeHexStr(pwd);
    }

    /**
     * 密码验证
     * @param password 待验证的密码
     * @param encryptedPassword 加密后的密码
     * @return
     */
    public static boolean valid(String password, String encryptedPassword) {
        byte[] encryptedPwd = HexUtil.decodeHex(encryptedPassword);
        //取出前20个字节盐值
        byte[] salt = ArrayUtil.sub(encryptedPwd, 0, SALT_BYTE_LENGTH);
        //20字节后为真正MD5后密码
        byte[] pwd = ArrayUtil.sub(encryptedPwd, SALT_BYTE_LENGTH, encryptedPwd.length);
        MD5 md5 = new MD5(salt, DIGEST_COUNT);
        byte[] digest = md5.digest(password, CHARSET);
        return Arrays.equals(digest, pwd);
    }

}
