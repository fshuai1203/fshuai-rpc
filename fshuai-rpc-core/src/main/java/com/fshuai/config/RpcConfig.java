package com.fshuai.config;

import com.fshuai.serializer.Serializer;
import com.fshuai.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "yu-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * mock服务
     */
    private boolean mock = false;

    /**
     * 定义序列化器
     */
    private String serializer = "jdk";

}
