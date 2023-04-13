package com.hmdp.controller;

import com.hmdp.dto.LoginDTO;
import com.hmdp.dto.Result;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     * @param phone
     * @return
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone) {
        return userService.sendCode(phone);
    }

    /**
     * 用户登陆功能
     * @param loginDTO
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

}
