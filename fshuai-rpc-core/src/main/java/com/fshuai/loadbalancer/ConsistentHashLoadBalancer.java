package com.fshuai.loadbalancer;

import com.fshuai.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer extends LoadBalancer {

    // 一致性Hash换，存放虚拟节点
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    // 虚拟节点数
    private static final int VIRTUAL_NODE_SIZE = 100;

    @Override
    public ServiceMetaInfo selectFromList(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList, int infoListSize) {

        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取调用请求的Hash值
        int hash = getHash(requestParams);

        // 选择最近且大雨等于调用请求Hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);

        if (entry == null) {
            // 如果大于等于调用请求hash值的虚拟节点为空，则返回首部的节点
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    /**
     * Hash算法
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
