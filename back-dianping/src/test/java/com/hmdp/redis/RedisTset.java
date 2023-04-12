package com.hmdp.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTset {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 字符串测试
     */
    @Test
    public void stringTest() throws Exception {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();

        // 值的设置和获取
        operations.set("user:name","小明");
        operations.set("user:sex","男");
        operations.set("user:change","小明的信息");
        System.out.println("姓名：" + operations.get("user:name"));
        System.out.println("性别：" + operations.get("user:sex"));
        System.out.println("信息：" + operations.get("user:change"));

        // 字符串追加
        operations.append("user:change", ", 我是追加到末尾的信息");
        System.out.println("信息：" + operations.get("user:change"));

        // 值长度
        System.out.println("信息长度：" + operations.size("user:change"));

        // 设置新值，返回旧值
        System.out.println("旧值：" + operations.getAndSet("user:name", "小天"));
        System.out.println("新值：" + operations.get("user:name"));

        //设置过期时间，2s失效，2s之内查询有结果，2s之后返回null
        operations.set("user:change","我会在2秒后失效",2, TimeUnit.SECONDS);
        System.out.println("信息(2秒内)：" + operations.get("user:change"));
        Thread.currentThread().sleep(2000);
        System.out.println("信息(2秒后)：" + operations.get("user:change"));
    }

    /**
     * 列表测试
     * @throws Exception
     */
    @Test
    public void listTest() throws Exception {
        ListOperations<String, String> operations = stringRedisTemplate.opsForList();

        // 值的设置和获取
        String[] user1 = new String[]{"小明","男","我是小明"};
        String[] user2 = new String[]{"小红","女","我是小红"};
        String[] user3 = new String[]{"小天","男","我是小天"};

        // 加在左边
        operations.leftPushAll("user:list:user1", user1);
        // 加在右边
        operations.rightPushAll("user:list:user2", user2);
        // 加在左边
        operations.leftPushAll("user:list:user3", user3);

        System.out.println(operations.range("user:list:user1",0,-1));
        System.out.println(operations.range("user:list:user2",0,-1));
        System.out.println(operations.range("user:list:user3",0,-1));

        System.out.println(operations.range("user:list:user1",1,-1));
        System.out.println(operations.range("user:list:user2",2,-1));
        System.out.println(operations.range("user:list:user3",3,-1));

    }

}
