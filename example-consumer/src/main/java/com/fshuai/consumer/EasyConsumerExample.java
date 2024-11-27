package com.fshuai.consumer;

import com.fshuai.bootstrap.ConsumerBootstrap;
import com.fshuai.model.User;
import com.fshuai.proxy.ServiceProxyFactory;
import com.fshuai.service.UserService;

public class EasyConsumerExample {

    public static void main(String[] args) {

        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("fshuai");
        User newUser = userService.getUser(user);

        // 调用
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null");
        }

        System.out.println(userService.gerNumber());
    }

}