package com.hmdp.utils;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisData {

    /**
     * Redis存放的数据
     */
    private Object data;

    /**
     * 数据的逻辑过期时间
     */
    private LocalDateTime expireTime;

}
