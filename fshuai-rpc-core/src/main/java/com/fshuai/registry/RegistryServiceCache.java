package com.fshuai.registry;

import com.fshuai.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;


    /**
     * 写缓存
     *
     * @param newServiceCache 新的缓存
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }

    /**
     * 读缓存
     */
    List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

}
