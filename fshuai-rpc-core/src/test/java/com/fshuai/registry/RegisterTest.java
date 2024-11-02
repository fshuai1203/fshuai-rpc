package com.fshuai.registry;

import com.fshuai.config.RegistryConfig;
import com.fshuai.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegisterTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registerConfig = new RegistryConfig();
        registerConfig.setAddress("http://localhost:2379");
        registry.init(registerConfig);
    }

    @Test
    public void heartBeat() throws Exception {
        // init已经执行心跳检测
        // 注册服务
        register();

        Thread.sleep(60 * 1000L);
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
    }

    @Test
    public void unRegister() {
        // 删除节点
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.unRegister(serviceMetaInfo);

        // 获取节点
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);

        // 检查节点是否删除
        Assert.assertEquals(1, serviceMetaInfoList.size());

    }

    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList);
    }
}

