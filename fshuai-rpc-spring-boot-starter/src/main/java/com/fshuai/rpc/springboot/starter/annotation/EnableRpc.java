package com.fshuai.rpc.springboot.starter.annotation;

import com.fshuai.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.fshuai.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.fshuai.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用RPC注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcConsumerBootstrap.class, RpcInitBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {


    /**
     * 是否需要启动服务器
     *
     */
    boolean needServer() default true;

}
