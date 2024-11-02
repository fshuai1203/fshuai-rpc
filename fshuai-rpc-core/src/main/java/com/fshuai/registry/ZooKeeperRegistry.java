package com.fshuai.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.fshuai.config.RegistryConfig;
import com.fshuai.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ZooKeeperRegistry implements Registry {

    private CuratorFramework client;

    ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;
    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";

    /**
     * 本机注册的节点key集合(用于维护续期)
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();


    @Override
    public void init(RegistryConfig registryConfig) {
        // 构建Client实例
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        // 构建ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            // 启动client和serviceDiscovery
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 注册到zk中
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        // 添加节点到本地缓存中
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registerKey);

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 从本地缓存移除
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
// 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        try {
            // 查询服务信息
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);

            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            // 写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void Destroy() {
        log.info("当前节点下线");
        // 下线节点（这一步可以不做，因为都是临时节点，服务下线，自然就被删掉了）
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放资源
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 不需要心跳机制，建立了临时节点，如果服务器故障，则临时节点直接丢失
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);
        if (newWatch) {
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges(((oldNode, node) -> registryServiceCache.clearCache()))
                            .build()
            );
        }
    }


    /**
     * 构建服务实例
     * <p>
     * 此方法根据服务的元信息构建一个服务实例对象ServiceInstance
     * 它将服务的主机和端口组合成服务地址，并将其作为服务实例的标识和地址
     * 如果构建过程中遇到异常，将抛出运行时异常
     *
     * @param serviceMetaInfo 服务的元信息，包含服务的关键字、主机和端口等信息
     * @return 返回构建好的服务实例对象ServiceInstance，包含服务的详细信息
     */
    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        // 组合服务主机和端口以形成完整的服务地址
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try {
            // 使用构建器模式创建ServiceInstance对象，并设置其属性
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress) // 设置服务实例的ID为服务地址
                    .name(serviceMetaInfo.getServiceKey()) // 设置服务实例的名称为服务关键字
                    .address(serviceAddress) // 设置服务实例的地址为服务地址
                    .payload(serviceMetaInfo) // 设置服务实例的有效载荷为服务的元信息
                    .build(); // 构建服务实例对象
        } catch (Exception e) {
            // 如果在构建过程中捕获到异常，将其包装成运行时异常并抛出
            throw new RuntimeException(e);
        }
    }
}
