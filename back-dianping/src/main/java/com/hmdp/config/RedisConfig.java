package com.hmdp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    // @Bean
    // public RedissonClient redissonClient() {
    //     Config config = new Config();
    //     String address = String.format("redis://%s:%d", host, port);
    //     config.useSingleServer().setAddress(address);
    //     if (StrUtil.isNotBlank(password)) {
    //         config.useSingleServer().setPassword(password);
    //     }
    //     return Redisson.create(config);
    // }
}
