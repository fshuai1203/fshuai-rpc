package com.fshuai.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.serializer.JdkSerializer;
import com.fshuai.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .args(args)
                .parameterTypes(method.getParameterTypes())
                .build();

        try {
            // 序列化
            byte[] bodyBytes = serializer.serializer(rpcRequest);
            // 发送请求
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:1203").body(bodyBytes).execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
