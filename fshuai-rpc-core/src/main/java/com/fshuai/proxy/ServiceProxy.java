package com.fshuai.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fshuai.RpcApplication;
import com.fshuai.config.RpcConfig;
import com.fshuai.constant.RpcConstant;
import com.fshuai.fault.retry.RetryStrategy;
import com.fshuai.fault.retry.RetryStrategyFactory;
import com.fshuai.fault.retry.RetryStrategyKeys;
import com.fshuai.fault.tolerant.TolerantStrategy;
import com.fshuai.fault.tolerant.TolerantStrategyFactory;
import com.fshuai.loadbalancer.LoadBalancer;
import com.fshuai.loadbalancer.LoadBalancerFactory;
import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.model.ServiceMetaInfo;
import com.fshuai.registry.Registry;
import com.fshuai.registry.RegistryFactory;
import com.fshuai.serializer.Serializer;
import com.fshuai.serializer.SerializerFactory;
import com.fshuai.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .args(args)
                .parameterTypes(method.getParameterTypes())
                .build();

        // 序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);

        // 从注册中心获取服务提供者的请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        // 构建ServiceKey
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        // 获取注册中心所有的服务节点
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 调用方法名(请求路径)作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        RpcResponse rpcResponse;
        try {
            //使用重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            // 发送TCP请求
            rpcResponse =
                    retryStrategy.doRetry(() ->
                            VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                    );

        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            Map<String, Object> requestTolerantParamMap = new HashMap<>();
            requestTolerantParamMap.put("rpcRequest",rpcRequest);
            requestTolerantParamMap.put("selectedServiceMetaInfo",selectedServiceMetaInfo);
            requestTolerantParamMap.put("serviceMetaInfoList",serviceMetaInfoList);
            requestTolerantParamMap.put("returnType",method.getReturnType());
            rpcResponse = tolerantStrategy.doTolerant(requestTolerantParamMap, e);
        }

        return rpcResponse.getData();
    }
}
