package com.fshuai.server.tcp;

import com.fshuai.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpServerHandler());

        // 监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("正在监听端口:" + port);
            } else {
                System.out.println("端口监听失败" + result.cause());
            }
        });
    }

}