package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.PasswordUtil;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone) {
        // 1. 检验手机号码
        if (StrUtil.isBlank(phone)) {
            // 如果为空
            return Result.fail("手机号码为空");
        }
        if (!Validator.isMobile(phone)) {
            // 如果不是手机号
            return Result.fail("手机号码格式错误");
        }

        // 2. 生成验证码
        String code = RandomUtil.randomNumbers(6);
        String codeKey = RedisConstants.LOGIN_CODE_KEY + phone;

        // 3. 防止用户过于频繁发送
        Long expire = stringRedisTemplate.opsForValue().getOperations().getExpire(codeKey);
        if (RedisConstants.LOGIN_CODE_TTL - expire < 60) {
            return Result.fail("操作过于频繁，请一分钟之后再次点击发送");
        }

        // 4. 调用短信发送逻辑
        boolean isSuccess = true;
        if (!isSuccess){
            return Result.fail("短信发送失败");
        }

        // 思考一下 登录信息存入session和redis的优缺点

        // 5. 短信发送成功，才将验证码保存到 redis 中
        stringRedisTemplate.opsForValue().set(codeKey, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.SECONDS);
        log.debug("send code {} to phone {}", code, phone);

        // 6. 返回发送成功
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
        // 1. 获取用户信息
        User user = lambdaQuery().eq(User::getPhone, phone).one();
        if (user == null) {
            // 用户不存在
            return Result.fail("用户不存在");
        }

        // 2. 验证密码
        if (!PasswordUtil.valid(password,user.getPassword())) {
            // 验证不通过
            return Result.fail("密码不正确");
        }

        // 3. 验证通过，返回token
        log.debug("{} login successfully", phone);
        return getToken(user);
    }

    private Result loginWithCode(String phone, String code) {
        // 1.从redis获取验证码
        String codeKey = RedisConstants.LOGIN_CODE_KEY + phone;
        String cacheCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (StrUtil.isBlank(cacheCode)) {
            // 验证码不存在
            return Result.fail("验证码不存在或已过期，请重新获取验证码");
        }

        // 2. 验证验证码
        if (!cacheCode.equals(code)) {
            // 验证不通过
            return Result.fail("验证码错误");
        }

        // 3. 验证通过
        User user = lambdaQuery().eq(User::getPhone, phone).one();
        if (user == null) {
            // 4. 用户不存在，创建用户
            user = createDefaultUserWithPhone(phone);
        }

        // 4. 登陆成功后，移除验证码
        if (stringRedisTemplate.hasKey(codeKey)) {
            stringRedisTemplate.delete(codeKey);
        }

        // 6. 验证通过，返回token
        log.debug("{} login successfully", phone);
        return getToken(user);
    }

    private User createDefaultUserWithPhone(String phone) {
        // 创建默认用户
        User user = new User();
        user.setPhone(phone);
        String nickName = SystemConstants.USER_DEFAULT_NICK_NAME_PREFIX + RandomUtil.randomString(10);
        user.setNickName(nickName);
        save(user);
        return user;
    }

    private Result getToken(User user){

        // 1. 过滤敏感信息
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        // 2. 生成随机token作为登入令牌, 去除-线
        String token = UUID.randomUUID().toString(true);

        // 3. 获取key
        String tokenKey =RedisConstants.LOGIN_USER_KEY + token;

        // 4. 转换成map
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fileName, fileValue) -> fileValue.toString()));

        // 思考一下 这里存入用户信息为什么用Hash类型

        // 5. 存入redis中并设置有效期
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        stringRedisTemplate.expire(tokenKey,RedisConstants.LOGIN_USER_TTL,TimeUnit.MINUTES);

        // 6. 存入本地ThreadLocal中
        UserHolder.addCurrentUser(userDTO);

        // 7 返回token
        return Result.ok(token);
    }

    @Override
    public Result logout(HttpServletRequest request) {
        // 1. 获取token
        String token = request.getHeader("authorization");
        // 2. 拼接token在redis的key
        String tokenKey =RedisConstants.LOGIN_USER_KEY + token;
        // 3. 判断token在redis是否存在
        if (stringRedisTemplate.hasKey(tokenKey)) {
            // 存在，则进行删除
            stringRedisTemplate.delete(tokenKey);
        }
        // 4. 删除本地线程的用户信息
        UserHolder.remove();
        // 5. 返回成功
        return Result.ok();
    }
}
