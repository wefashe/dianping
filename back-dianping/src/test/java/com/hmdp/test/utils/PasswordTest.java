package com.hmdp.test.utils;

import com.hmdp.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class PasswordTest {

    @Test
    public void test(){
        String password = "123456";
        log.info("原始密码：{}", password);
        String encryptedPassword = PasswordUtil.encrypt(password);
        log.info("生成不可逆16进制加密密码:{}", encryptedPassword);
        log.info("长度:{}", encryptedPassword.length());
        boolean isValid = PasswordUtil.valid(password, encryptedPassword);
        log.info("校验登录密码是否正常:{}", isValid);
        log.info("原始密码追加内容,在校验登录密码是否正常:{}",PasswordUtil.valid(password+"663",encryptedPassword));
    }

}
