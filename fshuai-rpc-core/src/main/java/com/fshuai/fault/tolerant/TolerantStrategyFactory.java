package com.fshuai.fault.tolerant;

import com.fshuai.spi.SpiLoader;

public class TolerantStrategyFactory {

    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }


}
