package com.fshuai.fault.retry;

import com.fshuai.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 重试
     * @param callable 回调函数，代表一类任务
     * @return RpcResponse
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;

}
