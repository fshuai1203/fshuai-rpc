package com.fshuai.registry;

import com.fshuai.spi.SpiLoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {

//    static {
//        // 使用Spi加载所有注册中心类
//        SpiLoader.load(Register.class);
//    }

    //默认注册中心
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();


    /**
     * 获取实例
     *
     * @param key 对于注册中心的key
     * @return 注册中心
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
