package com.fshuai.provider;

import com.fshuai.RpcApplication;
import com.fshuai.registry.LocalRegistry;
import com.fshuai.server.HttpServer;
import com.fshuai.server.VertxHttpServer;
import com.fshuai.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {

        // RPC框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

    }

}
