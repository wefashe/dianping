package com.hmdp.utils;

public final class RedisConstants {

    // 登录验证码及其过期时间 2分钟
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 60 * 2L;
    // 登录token及其过期时间
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

}
