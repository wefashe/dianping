package com.hmdp.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_CODE_TTL;
import static com.hmdp.utils.SystemConstants.USER_DEFAULT_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone) {
        // 1. 检验手机号码
        if (StrUtil.isBlank(phone)) {
            return Result.fail("手机号码为空");
        }
        if (!Validator.isMobile(phone)) {
            return Result.fail("手机号码格式错误");
        }
        // 2. 生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 3. 将验证码保存到 session/redis 中
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 4. 发送验证码到手机
        log.debug("send code {} to phone {}", code, phone);
        // 5. 返回发送成功
        return Result.ok();

    }

    @Override
    public Result login(LoginDTO loginDTO) {
        // 1. 检验手机号、验证码或密码
        if (StrUtil.isBlank(loginDTO.getPhone())) {
            return Result.fail("手机号码为空");
        }
        if (!Validator.isMobile(loginDTO.getPhone())) {
            return Result.fail("手机号码格式错误");
        }
        if (StrUtil.isBlank(loginDTO.getCode()) && StrUtil.isBlank(loginDTO.getPassword())) {
            return Result.fail("验证码或手机号码为空");
        }
        // 2. 验证码为空，密码存在，使用密码登录
        if (StrUtil.isBlank(loginDTO.getCode())) {
            return loginWithPassword(loginDTO.getPhone(), loginDTO.getPassword());
        }
        // 3. 验证码存在，不管密码是否存在，都可登录
        return loginWithCode(loginDTO.getPhone(), loginDTO.getCode());
    }

    private Result loginWithPassword(String phone, String password) {
        User user = lambdaQuery().eq(User::getPhone, phone).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if (!PasswordUtil.valid(password,user.getPassword())) {
            return Result.fail("密码不正确");
        }
        return Result.ok();
    }

    private Result loginWithCode(String phone, String code) {
        // 1.从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if (StrUtil.isBlank(cacheCode)) {
            return Result.fail("服务异常，请重新获取验证码");
        }
        if (!cacheCode.equals(code)) {
            return Result.fail("验证码错误");
        }
        User user = lambdaQuery().eq(User::getPhone, phone).one();
        if (user == null) {
            user = createDefaultUserWithPhone(phone);
        }
        String token = UUID.randomUUID().toString(true);

        return Result.ok(token);
    }

    private User createDefaultUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_DEFAULT_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        save(user);
        return user;
    }
}
