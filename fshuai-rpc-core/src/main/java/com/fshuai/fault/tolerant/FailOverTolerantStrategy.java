package com.fshuai.fault.tolerant;

import cn.hutool.core.collection.CollUtil;
import com.fshuai.RpcApplication;
import com.fshuai.config.RpcConfig;
import com.fshuai.fault.retry.RetryStrategy;
import com.fshuai.fault.retry.RetryStrategyFactory;
import com.fshuai.loadbalancer.LoadBalancer;
import com.fshuai.loadbalancer.LoadBalancerFactory;
import com.fshuai.model.RpcRequest;
import com.fshuai.model.RpcResponse;
import com.fshuai.model.ServiceMetaInfo;
import com.fshuai.server.tcp.VertxTcpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 转移到其他服务节点-容错策略
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        ServiceMetaInfo selectedServiceMetaInfo = (ServiceMetaInfo) context.get("selectedServiceMetaInfo");
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("serviceMetaInfoList");

        // 移除失败节点
        removeFailNode(selectedServiceMetaInfo, serviceMetaInfoList);

        RpcResponse rpcResponse = null;
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 调用方法名(请求路径)作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());


        while (!serviceMetaInfoList.isEmpty()){

            ServiceMetaInfo currentServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println("获取节点" + currentServiceMetaInfo);

            try {
                //使用重试机制
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                // 发送TCP请求
                rpcResponse =
                        retryStrategy.doRetry(() ->
                                VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                        );

                return rpcResponse;
            } catch (Exception exception) {
                // 移除失败节点
                removeFailNode(currentServiceMetaInfo, serviceMetaInfoList);
            }
        }

        // 调用失败
        throw new RuntimeException("服务报错", e);
    }

    /**
     * 移除失败节点
     *
     * @param currentServiceMetaInfo 当前节点
     * @param serviceMetaInfoList    节点列表
     */
    private void removeFailNode(ServiceMetaInfo currentServiceMetaInfo, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isNotEmpty(serviceMetaInfoList)) {
            Iterator<ServiceMetaInfo> iterator = serviceMetaInfoList.iterator();
            while (iterator.hasNext()) {
                ServiceMetaInfo next = iterator.next();
                if (currentServiceMetaInfo.getServiceNodeKey().equals(next.getServiceNodeKey())) {
                    iterator.remove();
                }
            }
        }
    }
}
