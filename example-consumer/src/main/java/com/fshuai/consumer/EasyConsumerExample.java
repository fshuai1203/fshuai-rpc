package com.fshuai.consumer;

import com.fshuai.model.User;
import com.fshuai.service.UserService;

public class EasyConsumerExample {

    public static void main(String[] args) {

        UserService userService = new UserServiceProxy();

        User user = new User();
        user.setName("fshuai");
        User newUser = userService.getUser(user);

        if (newUser!=null){
            System.out.println(newUser.getName());
        }else {
            System.out.println("user==null");
        }
    }

}