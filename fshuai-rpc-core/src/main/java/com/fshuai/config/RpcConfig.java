package com.fshuai.config;

import com.fshuai.loadbalancer.LoadBalancer;
import com.fshuai.loadbalancer.LoadBalancerKeys;
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

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

}
