package com.fshuai.fault.retry;

public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO = "no";

    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "fixedInterval";

    /**
     * 指数退避重试策略
     */
    String EXPONENTIAL = "exponential";

}
