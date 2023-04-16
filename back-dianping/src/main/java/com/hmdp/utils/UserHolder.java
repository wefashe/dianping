package com.hmdp.utils;

import com.hmdp.dto.UserDTO;

public class UserHolder {

    /**
     * 保存用户对象的ThreadLocal  在拦截器操作 添加、删除相关用户数据
     */
    private static final ThreadLocal<UserDTO> currentUser = new ThreadLocal<UserDTO>();

    public static void addCurrentUser(UserDTO user){
        currentUser.set(user);
    }

    public static UserDTO getCurrentUser(){
        return currentUser.get();
    }

    public static void remove(){
        currentUser.remove();
    }

}
