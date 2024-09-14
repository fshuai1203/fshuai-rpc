package com.fshuai.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.model.User;
import com.fshuai.serializer.JdkSerializer;
import com.fshuai.serializer.Serializer;
import com.fshuai.service.UserService;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {

        Serializer serializer = new JdkSerializer();

        // 发起请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .args(new Object[]{user})
                .parameterTypes(new Class[]{User.class})
                .build();

        try {
            byte[] bodyBytes = serializer.serializer(rpcRequest);
            byte[] result;
            // 发送请求并获得序列化的响应
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:1203")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }

            // 反序列化响应
            RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
            return (User) rpcResponse.getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
