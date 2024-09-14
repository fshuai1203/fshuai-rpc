package com.fshuai.provider;

import com.fshuai.model.User;
import com.fshuai.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名:" + user.getName());
        user.setName(user.getName() + "@-@");
        return user;
    }
}
