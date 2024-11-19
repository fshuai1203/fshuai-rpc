package com.fshuai.loadbalancer;

import com.fshuai.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;

public class MinimumActiveLoadBalancer implements LoadBalancer {

    // 用于存储每个Service的使用次数
    private static final Map<ServiceMetaInfo, Integer> serviceUseCountMap = new HashMap<>();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 创建一个比较器，按使用次数升序排序
        Comparator<ServiceMetaInfo> comparator = (s1, s2) -> {
            int count1 = serviceUseCountMap.getOrDefault(s1, 0);
            int count2 = serviceUseCountMap.getOrDefault(s2, 0);
            return Integer.compare(count1, count2);
        };

        // 使用优先队列（最小堆）找到使用次数最少的服务
        PriorityQueue<ServiceMetaInfo> priorityQueue = new PriorityQueue<>(comparator);
        priorityQueue.addAll(serviceMetaInfoList);

        // 选择使用次数最少的服务
        ServiceMetaInfo selectedServiceMetaInfo = priorityQueue.poll();

        // 更新使用次数
        serviceUseCountMap.put(selectedServiceMetaInfo, serviceUseCountMap.getOrDefault(selectedServiceMetaInfo, 0) + 1);

        return selectedServiceMetaInfo;
    }
}

