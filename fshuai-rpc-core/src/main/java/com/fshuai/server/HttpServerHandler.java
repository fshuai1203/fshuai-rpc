package com.fshuai.server;

import com.fshuai.RpcApplication;
import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.registry.LocalRegistry;
import com.fshuai.serializer.Serializer;
import com.fshuai.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 记录日志
        System.out.println("Received request:" + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            // 反序列化得到请求实体
            byte[] bodyBytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 构造响应结果信息
            RpcResponse rpcResponse = new RpcResponse();

            // 如果请求为空，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            // 请求非空，通过反射调用服务实现类
            try {
                // 通过注册器获取服务实现类
                Class<?> implClass = LocalRegistry.getRegister(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");

            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(request, rpcResponse, serializer);
        });

    }

    /**
     * 处理RPC响应的函数
     * 将RPC响应对象序列化为字节流，并通过HTTP响应返回
     *
     * @param request     HttpServerRequest对象，表示接收到的HTTP请求
     * @param rpcResponse RpcResponse对象，表示RPC调用的结果
     * @param serializer  Serializer对象，用于对象的序列化
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        // 获取HTTP响应对象，并设置响应内容类型为application/json
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            // 使用serializer将RPC响应对象序列化为字节流
            byte[] serializered = serializer.serialize(rpcResponse);
            // 将序列化后的字节流写入HTTP响应中，结束响应
            httpServerResponse.end(Buffer.buffer(serializered));
        } catch (IOException e) {
            // 如果序列化过程中发生IO异常，打印异常栈信息
            e.printStackTrace();
            // 结束响应，不发送任何内容
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
