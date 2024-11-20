package com.fshuai.fault.retry;

import com.fshuai.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避间隔-重试策略
 */
@Slf4j
public class ExponentialRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)// 重试条件：出现Exception异常
                .withWaitStrategy(WaitStrategies.exponentialWait(3L, TimeUnit.SECONDS))// 重试等待策略:固定时间策略
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))// 重试停止策略：重试三次
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            // 重试工作：打印重试次数
                            log.info("重试次数{}", attempt.getAttemptNumber());
                        }
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
