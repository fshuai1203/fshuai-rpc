package com.fshuai.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    /**
     * 根据指定的服务类生成代理对象
     * 该方法使用Java动态代理来创建一个服务类的代理实例，代理对象可以在方法调用时
     * 动态决定方法的执行逻辑，这里主要用于AOP（面向切面编程）的实现，比如在方法执行前
     * 后添加额外的操作
     *
     * @param serviceClass 服务类类型，即接口类型该接口定义了将被代理对象所支持的方法
     * @param <T>          泛型标记，表示服务类的类型
     * @return 服务类的代理对象，类型为T代理对象可以在客户端代替真实对象进行操作
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        // 创建代理对象，使用serviceClass的类加载器，以serviceClass为接口，并使用ServiceProxy作为调用处理器
        // ServiceProxy负责处理代理对象上方法的调用，将调用转发给真实对象或执行其他逻辑
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

}
