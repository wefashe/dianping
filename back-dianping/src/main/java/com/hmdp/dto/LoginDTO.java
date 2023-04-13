package com.hmdp.dto;

import lombok.Data;

@Data
public class LoginDTO {

    /**
     * 登陆页面输入的手机号
     */
    private String phone;

    /**
     * 登陆页面输入的验证码
     */
    private String code;

    /**
     * 登陆页面上输入的密码
     */
    private String password;

}
