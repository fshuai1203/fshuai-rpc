package com.fshuai.server.tcp;


import cn.hutool.core.util.IdUtil;
import com.fshuai.RpcApplication;
import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.model.ServiceMetaInfo;
import com.fshuai.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx Tcp请求客户端
 */
public class VertxTcpClient {


    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {

        // 发送TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();

        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        // 建立连接失败
                        System.err.println("Failed to connect to TCP server");
                        // 抛出异常，让重试策略捕捉到异常
                        responseFuture.completeExceptionally(new RuntimeException("Failed to connect to TCP server"));
                        return;
                    }

                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum
                            .getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 接收响应
                    // 使用TCPBufferHandlerWrapper包装，避免Vertx的Buffer对象的粘包和少包
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        // 处理响应
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage
                                    = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            // 用于将异步转为同步请求
                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    socket.handler(bufferHandlerWrapper);
                });

        RpcResponse rpcResponse = responseFuture.get();

        // 关闭连接
        netClient.close();
        return rpcResponse;

    }

}
