package org.example.server.log;

import org.example.server.log.domain.LogInfo;
import org.example.server.web.utils.LoginUtils;

import java.lang.reflect.Method;

public interface BizLogHandler {
    LogInfo convertToLog(Class<?> classType, Method method, Object[] args);

    boolean supportsMethod(Class<?> classType, Method method);

    default LogInfo notSupportMethod(Method method) {
        throw new IllegalStateException("Not support method " + method.getName());
    }

    default String operator() {
        return LoginUtils.getNickname();
    }

    default Long operatorId() {
        return LoginUtils.getUserId();
    }

    default String ip() {
        return LoginUtils.getIp();
    }
    // 泛型方法，将 args[0] 转换为指定类型 T
    default <T> T argsToObject(Object[] args, Class<T> tClass) {
        if (args.length > 0 && tClass.isInstance(args[0])) {
            return tClass.cast(args[0]); // 安全地将 args[0] 转换为 T 类型
        }
        throw new IllegalArgumentException("参数类型不匹配或参数列表为空");
    }
}
