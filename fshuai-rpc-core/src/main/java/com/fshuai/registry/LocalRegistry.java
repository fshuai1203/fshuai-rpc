package com.fshuai.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private static final Map<String, Class> map = new ConcurrentHashMap<>();


    /**
     * 注册服务
     *
     * @param serviceName 服务名称
     * @param implClass   具体实现类
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return 服务实现类
     */
    public static Class<?> getService(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName 要删除的服务名称
     */
    public static void removeRegister(String serviceName) {
        map.remove(serviceName);
    }

}
