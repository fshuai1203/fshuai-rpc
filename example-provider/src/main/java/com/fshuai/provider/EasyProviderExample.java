package com.fshuai.provider;

import com.fshuai.RpcApplication;
import com.fshuai.register.LocalRegister;
import com.fshuai.server.HttpServer;
import com.fshuai.server.VertxHttpServer;
import com.fshuai.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {

        // RPC框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

    }

}
