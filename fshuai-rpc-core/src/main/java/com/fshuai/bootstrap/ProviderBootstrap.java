package com.fshuai.bootstrap;

import com.fshuai.RpcApplication;
import com.fshuai.config.RegistryConfig;
import com.fshuai.config.RpcConfig;
import com.fshuai.model.ServiceMetaInfo;
import com.fshuai.model.ServiceRegisterInfo;
import com.fshuai.registry.LocalRegistry;
import com.fshuai.registry.Registry;
import com.fshuai.registry.RegistryFactory;
import com.fshuai.server.HttpServer;
import com.fshuai.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {

        // RPC框架初始化
        RpcApplication.init();

        // 获取配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            // 注册服务
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            // 获取注册中心
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
        }
        // 启动tcp服务
        HttpServer httpServer = new VertxTcpServer();

        httpServer.doStart(rpcConfig.getServerPort());
    }

}
