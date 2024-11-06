package com.fshuai.protocol;


import com.fshuai.serializer.Serializer;
import com.fshuai.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {

    /**
     * 按照自定义的消息结构快进行编码
     *
     * @param protocolMessage 消息对象
     * @return 编码后的buffer
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws RuntimeException, IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = protocolMessage.getHeader();

        // 按照定义的格式，依次写入Buffer
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不存在");
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        // 对请求body进行序列化
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;

    }

}
