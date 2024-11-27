package com.fshuai;

import com.fshuai.model.User;
import com.fshuai.rpc.springboot.starter.annotation.RpcReference;
import com.fshuai.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test(){
        User user = new User();
        user.setName("fshuai");
        User newUser = userService.getUser(user);
        System.out.println(newUser.getName());
    }

}
