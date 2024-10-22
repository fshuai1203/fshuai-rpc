package com.fshuai.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 根据类类型和前缀加载配置
     *
     * @param tClass 配置类的类型
     * @param prefix 配置的前缀
     * @return 返回加载的配置实例
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        // 通过重载的方法调用来实现相同的功能，传递空字符串作为默认分隔符
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置文件并返回配置对象
     *
     * @param tClass      配置对象的类类型，用于将Properties文件转换为对象
     * @param prefix      配置键的前缀，用于从Properties文件中精确加载部分配置
     * @param environment 环境标识符，用于区分不同的环境配置文件，如"dev"、"prod"等
     * @return 返回加载的配置对象，该对象的类型由tClass指定
     *
     * 说明：
     * 1. 该方法用于根据提供的类类型和前缀，从特定的Properties配置文件中加载配置，并转换为指定的Java对象。
     * 2. 支持根据环境标识符加载不同环境的配置文件，如果environment为空或空白，则默认加载基础配置文件。
     * 3. 配置文件的命名规则为：application-{environment}.properties，例如：application-dev.properties。
     * 4. 使用Props工具类来处理Properties文件的加载和转换。
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        // 初始化配置文件名构建器，基础名为"application"
        StringBuilder configFileBuilder = new StringBuilder("application");
        // 如果环境标识符不为空或空白，则将其追加到配置文件名中，用"-"分隔
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        // 追加文件扩展名".properties"，完成配置文件名构建
        configFileBuilder.append(".properties");
        // 使用构建的配置文件名创建Props对象，用于后续的配置加载
        Props props = new Props(configFileBuilder.toString());
        // 将Props对象转换为指定类型的Java对象，并返回
        return props.toBean(tClass, prefix);
    }

}
