package com.fshuai.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 */
public class SerializerFactory1 {

    /**
     * 序列化映射(用于实现单例)
     */
    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {
        {
            put(SerializerKeys.JSON, new JsonSerializer());
            put(SerializerKeys.HESSIAN, new HessianSerializer());
            put(SerializerKeys.JDK, new JsonSerializer());
            put(SerializerKeys.KRYO, new KryoSerializer());
        }
    };

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);

    /**
     * 获取实例
     */
    public static Serializer getInstance(String key) {
        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }

}
