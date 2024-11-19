package com.fshuai.loadbalancer;

import com.fshuai.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 */
public class RoundRobinLoadBalancer extends LoadBalancer {

    /**
     * 当前轮询的下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo selectFromList(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList, int infoListSize) {
        // 多个服务，取模算法轮询
        int index = currentIndex.getAndIncrement() % infoListSize;
        return serviceMetaInfoList.get(index);
    }
}
