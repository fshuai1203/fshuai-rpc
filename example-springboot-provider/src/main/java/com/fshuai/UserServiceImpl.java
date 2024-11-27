package com.fshuai;

import com.fshuai.model.User;
import com.fshuai.rpc.springboot.starter.annotation.RpcService;
import com.fshuai.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名:" + user.getName());
        user.setName(user.getName() + "@-@");
        return user;
    }
}
