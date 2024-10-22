package com.fshuai.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {

        // 创建vert.x实例
        Vertx vertx = Vertx.vertx();

        // 创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 创建拦截器
        server.requestHandler(new HttpServerHandler());

        // 监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("正在监听端口:" + port);
            } else {
                System.out.println("端口监听失败☹️" + result.cause());
            }
        });
    }
}
