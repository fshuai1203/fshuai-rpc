package com.fshuai.fault.tolerant;

import com.fshuai.model.RpcResponse;
import org.checkerframework.checker.units.qual.C;

import java.util.Map;

/**
 * 降级到其他服务-容错策略
 */
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        Class returnType = (Class) context.get("returnType");
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setData(getDefaultObject(returnType));
        rpcResponse.setDataType(returnType);
        return rpcResponse;
    }

    /**
     * 生成指定类型的默认值对象（可自行完善默认值逻辑）
     *
     * @param type 返回对象类型
     * @return 返回默认值
     */
    private Object getDefaultObject(Class<?> type) {
        // 基本类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == byte.class) {
                return (byte) 0;
            } else if (type == char.class) {
                return (char) 0;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == float.class) {
                return 0.0f;
            } else if (type == double.class) {
                return 0.0d;
            }
        }
        // 对象类型
        if (type == String.class) {
            return "";
        } else if (type == Character.class) {
            return (char) 0;
        } else if (type == Byte.class) {
            return (byte) 0;
        } else if (type == Short.class) {
            return (short) 0;
        } else if (type == Integer.class) {
            return 0;
        } else if (type == Long.class) {
            return 0L;
        } else if (type == Float.class) {
            return 0.0f;
        } else if (type == Double.class) {
            return 0.0d;
        } else if (type == Boolean.class) {
            return false;
        }
        // 其他对象类型
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("无法创建类型 " + type.getName() + " 的默认实例", e);
        }
    }

}
