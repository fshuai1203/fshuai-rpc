package com.fshuai.service;

import com.fshuai.model.User;

public interface UserService {

    User getUser(User user);

    default short gerNumber() {
        return 1;
    }

}
