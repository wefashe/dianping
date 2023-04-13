package com.hmdp.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class MybatisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = String.format("redis://%s:%d", host, port);
        config.useSingleServer().setAddress(address);
        if (StrUtil.isNotBlank(password)) {
            config.useSingleServer().setPassword(password);
        }
        return Redisson.create(config);
    }

}
