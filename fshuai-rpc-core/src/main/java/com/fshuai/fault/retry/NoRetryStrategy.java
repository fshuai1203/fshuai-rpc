package com.fshuai.fault.retry;

import com.fshuai.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试-重试策略
 */
public class NoRetryStrategy implements RetryStrategy {


    /**
     * 重试
     * @param callable 回调函数，代表一类任务
     * @return 直接调用一次并返回
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
