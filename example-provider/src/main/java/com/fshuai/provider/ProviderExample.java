package com.fshuai.provider;

import com.fshuai.RpcApplication;
import com.fshuai.config.RegistryConfig;
import com.fshuai.config.RpcConfig;
import com.fshuai.model.ServiceMetaInfo;
import com.fshuai.registry.LocalRegistry;
import com.fshuai.registry.Registry;
import com.fshuai.registry.RegistryFactory;
import com.fshuai.server.HttpServer;
import com.fshuai.server.VertxHttpServer;
import com.fshuai.service.UserService;

public class ProviderExample {
    public static void main(String[] args) {

        // RPC框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        // 获取注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        // 注册服务信息
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        // 这里的地址和端口号都是服务的，而不是注册中心的
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(rpcConfig.getServerPort());

    }

}
