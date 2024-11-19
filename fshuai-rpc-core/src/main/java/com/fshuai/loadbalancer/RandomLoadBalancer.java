package com.fshuai.loadbalancer;

import com.fshuai.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器
 */
public class RandomLoadBalancer extends LoadBalancer {

    private final Random random = new Random();


    @Override
    public ServiceMetaInfo selectFromList(Map<String, Object> requestParams,
                                          List<ServiceMetaInfo> serviceMetaInfoList,
                                          int infoListSize) {
        return serviceMetaInfoList.get(random.nextInt() % infoListSize);
    }
}
