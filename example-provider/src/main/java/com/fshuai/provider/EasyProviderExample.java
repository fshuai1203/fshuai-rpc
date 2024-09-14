package com.fshuai.provider;

import com.fshuai.register.LocalRegister;
import com.fshuai.server.HttpServer;
import com.fshuai.server.VertxHttpServer;
import com.fshuai.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {

        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(1203);

    }

}
