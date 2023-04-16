package com.hmdp.controller;

import com.hmdp.dto.LoginDTO;
import com.hmdp.dto.Result;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 退出登录功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(){
        UserHolder.remove();
        return Result.ok();
    }

    /**
     *
     * @return
     */
    @GetMapping("/me")
    public Result me(){
        return Result.ok(UserHolder.getCurrentUser());
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        return Result.ok(userInfoService.getById(userId));
    }

}
