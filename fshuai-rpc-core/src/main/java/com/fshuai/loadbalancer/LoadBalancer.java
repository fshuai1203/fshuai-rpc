package com.fshuai.loadbalancer;

import com.fshuai.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡接口(消费者使用)
 */
public abstract class LoadBalancer {

    /**
     * 选择服务调用
     *
     * @param requestParams       请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 只有一个服务，无需轮询
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }

        // 多个服务，使用不同的算法
        return selectFromList(requestParams, serviceMetaInfoList, size);
    }

    abstract ServiceMetaInfo selectFromList(Map<String, Object> requestParams,
                                            List<ServiceMetaInfo> serviceMetaInfoList,
                                            int infoListSize);
}
