package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface IUserService extends IService<User> {

    /**
     * 发送手机验证码
     * @param phone 手机号码
     * @return
     */
    Result sendCode(String phone);

    /**
     * 用户登录功能
     * @param loginDTO 包含 手机号、验证码或密码
     * @return
     */
    Result login(LoginDTO loginDTO);

    /**
     * 退出登录功能
     * @param request
     * @return
     */
    Result logout(HttpServletRequest request);
}
