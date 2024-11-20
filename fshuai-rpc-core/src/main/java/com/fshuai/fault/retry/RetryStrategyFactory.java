package com.fshuai.fault.retry;

import com.fshuai.spi.SpiLoader;

public class RetryStrategyFactory {

    /**
     * 默认重试器
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 获取实例
     * @param key Retry Strategy Key
     * @return RetryStrategy实例
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}
