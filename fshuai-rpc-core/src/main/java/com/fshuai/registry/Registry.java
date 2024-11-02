package com.fshuai.registry;

import com.fshuai.config.RegistryConfig;
import com.fshuai.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心
 */
public interface Registry {

    /**
     * 注册中心初始化
     *
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务(服务端)
     *
     * @param serviceMetaInfo 服务元数据
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务
     *
     * @param serviceMetaInfo-服务元数据
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     *
     * @param serviceKey-服务键
     * @return 服务元信息列表
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void Destroy();

    /**
     * 心跳检测(服务端调用)
     */
    void heartBeat();

    /**
     * 监听（消费端调用）
     * @param serviceNodeKey 服务器节点键名
     */
    void watch(String serviceNodeKey);
}
