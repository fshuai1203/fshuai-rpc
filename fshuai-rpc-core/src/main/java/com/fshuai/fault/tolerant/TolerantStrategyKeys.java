package com.fshuai.fault.tolerant;

/**
 * 容错策略键名常量
 */
public interface TolerantStrategyKeys {

    /**
     * 故障降级
     */
    String FAIL_BACK = "failBack";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";


}
