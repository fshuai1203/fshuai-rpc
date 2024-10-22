package com.fshuai.server;

/**
 * 服务接口
 */
public interface HttpServer {

    /**
     * 开启服务
     *
     * @param port 端口号
     */
    void doStart(int port);

}
