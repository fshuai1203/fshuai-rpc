package com.fshuai.rpc.springboot.starter.bootstrap;

import com.fshuai.RpcApplication;
import com.fshuai.config.RegistryConfig;
import com.fshuai.config.RpcConfig;
import com.fshuai.model.ServiceMetaInfo;
import com.fshuai.registry.LocalRegistry;
import com.fshuai.registry.Registry;
import com.fshuai.registry.RegistryFactory;
import com.fshuai.rpc.springboot.starter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Rpc 服务提供者启动
 */
public class RpcProviderBootstrap implements BeanPostProcessor {


    /**
     * 在Bean初始化之后进行后处理
     *
     * @param bean 初始化后的Bean实例
     * @param beanName Bean的名称
     * @return 处理后的Bean实例
     * @throws BeansException 如果处理过程中发生异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 获取扫描服务的注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 需要注册服务
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }

            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 获取配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            // 注册服务到注册中心
            // 获取注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

            // 注册服务信息
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            // 这里的地址和端口号都是服务的，而不是注册中心的
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

    }
}
