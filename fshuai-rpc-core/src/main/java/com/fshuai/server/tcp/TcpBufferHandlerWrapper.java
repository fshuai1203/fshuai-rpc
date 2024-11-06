package com.fshuai.server.tcp;

import com.fshuai.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }


    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    /**
     * 初始化记录解析器
     * 该方法用于创建一个记录解析器，并设置其处理逻辑
     * 解析器用于处理固定长度的消息头和可变长度的消息体
     *
     * @param bufferHandler 当解析器接收到数据时调用的处理器
     * @return 返回初始化后的RecordParser对象
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {

        //RecordParser.newFixed 是一个静态工厂方法，用于创建一个固定长度记录的解析器。
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        // 设置解析器的目标
        parser.setOutput(new Handler<Buffer>() {

            // 初始化
            int size = -1;
            // 一次完整的读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (-1 == size) {
                    // 读取消息体长度
                    size = buffer.getInt(13);
                    // 设置到parser
                    parser.fixedSizeMode(size);
                    // 写入头信息到结果中
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接完整的buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }
}
