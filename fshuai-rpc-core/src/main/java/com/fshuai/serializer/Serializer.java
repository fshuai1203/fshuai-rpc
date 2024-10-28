package com.fshuai.serializer;

import java.io.IOException;

public interface Serializer {

    /**
     * 序列化
     * @param object 要序列化的对象
     * @return 序列化后的字节数组
     * @param <T> 对象范型
     * @throws IOException IO异常
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * @param bytes 要反序列化的字节数组
     * @param clazz 反序列化后的对象类型
     * @return 反序列化后的对象
     * @param <T> 对象范型
     * @throws IOException IO异常
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

}
