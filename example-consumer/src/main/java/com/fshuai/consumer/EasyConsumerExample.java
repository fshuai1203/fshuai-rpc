package com.fshuai.consumer;

import com.fshuai.config.RpcConfig;
import com.fshuai.model.User;
import com.fshuai.proxy.ServiceProxyFactory;
import com.fshuai.service.UserService;
import com.fshuai.utils.ConfigUtils;

public class EasyConsumerExample {

    public static void main(String[] args) {

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("fshuai");
        User newUser = userService.getUser(user);

        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null");
        }

        System.out.println(userService.gerNumber());
    }

}