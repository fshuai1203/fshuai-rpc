package com.fshuai.serializer;

import java.io.*;

public class JdkSerializer implements Serializer {
    /**
     * 将对象序列化为字节数组
     *
     * @param object 要序列化的对象，泛型参数确保可以序列化任何类型
     * @return 序列化后的字节数组
     * @throws IOException 如果序列化过程中发生错误
     */
    @Override
    public <T> byte[] serializer(T object) throws IOException {

        // 创建字节数组输出流，用于存储序列化后的字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 创建对象输出流，用于将对象写入输出流中
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // 将对象写入对象输出流中，实现对象的序列化
        objectOutputStream.writeObject(object);

        // 关闭对象输出流，释放资源
        objectOutputStream.close();

        // 返回序列化后的字节数组
        return outputStream.toByteArray();
    }


    /**
     * 泛型方法，用于反序列化字节数组为指定的类对象
     *
     * @param bytes 要反序列化的字节数组
     * @param clazz 要反序列化成的类的Class对象
     * @return 反序列化后的对象
     * @throws IOException 如果在反序列化过程中发生输入输出异常
     */
    @Override
    public <T> T deserializer(byte[] bytes, Class<T> clazz) throws IOException {
        // 将字节数组转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // 使用对象输入流读取输入流中的对象
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        try {
            // 读取对象并将其转换为指定的泛型类型T
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            // 如果指定类不存在，则抛出运行时异常
            throw new RuntimeException(e);
        } finally {
            // 确保关闭对象输入流以释放资源
            objectInputStream.close();
        }
    }

}
