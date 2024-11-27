package com.fshuai.rpc.springboot.starter.bootstrap;

import com.fshuai.RpcApplication;
import com.fshuai.config.RpcConfig;
import com.fshuai.rpc.springboot.starter.annotation.EnableRpc;
import com.fshuai.server.HttpServer;
import com.fshuai.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc框架启动
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * 注册Bean定义
     *
     * @param importingClassMetadata 当前导入类的元数据
     * @param registry Bean定义的注册表
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableRpc 注解的属性值
        boolean needServer
                = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        // RPC框架初始化(配置和注册中心)
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            // 启动tcp服务
            HttpServer httpServer = new VertxTcpServer();
            httpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动 server");
        }
    }
}
