package com.fshuai.server.tcp;


import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.protocol.ProtocolMessage;
import com.fshuai.protocol.ProtocolMessageDecoder;
import com.fshuai.protocol.ProtocolMessageEncoder;
import com.fshuai.protocol.ProtocolMessageTypeEnum;
import com.fshuai.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    /**
     * 处理网络套接字事件
     * 当有数据到达时，此方法将被调用
     *
     * @param netSocket 网络套接字对象，用于与客户端进行通信
     */
    @Override
    public void handle(NetSocket netSocket) {
        // 创建一个TCP缓冲处理器包装器，用于处理接收到的数据缓冲区
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                // 解码缓冲区数据为协议消息对象
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                // 如果解码过程中出现IO异常，抛出运行时异常
                throw new RuntimeException("协议消息解码错误");
            }
            // 获取解码后的RPC请求对象
            RpcRequest rpcRequest = protocolMessage.getBody();

            // 处理请求
            // 构造响应对象
            RpcResponse rpcResponse = new RpcResponse();

            // 如果请求对象为空，构造错误响应并发送
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                netSocket.write(doResponse(protocolMessage, rpcResponse));
                return;
            }
            try {
                // 获取要调用的服务类，利用反射进行调用
                Class<?> implClass = LocalRegistry.getService(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                // 设置响应数据和状态
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                // 如果调用过程中出现异常，设置响应的错误信息和异常对象
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 发送响应
            netSocket.write(doResponse(protocolMessage, rpcResponse));
        });

        // 设置网络套接字的数据处理 handler
        netSocket.handler(bufferHandlerWrapper);
    }

    private Buffer doResponse(ProtocolMessage<RpcRequest> protocolMessage, RpcResponse rpcResponse) {
        ProtocolMessage.Header header = protocolMessage.getHeader();

        // 更改头部类型
        header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());


        ProtocolMessage<RpcResponse> responseProtocolMessage
                = new ProtocolMessage<RpcResponse>(header, rpcResponse);
        try {
            // 编码
            Buffer encodeBuffer = ProtocolMessageEncoder.encode(responseProtocolMessage);
            return encodeBuffer;
        } catch (IOException e) {
            throw new RuntimeException("协议消息编码错误");
        }
    }

}
