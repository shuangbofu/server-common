package org.example.server.log.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.server.log.BizLogHandler;
import org.example.server.log.domain.LogInfo;
import org.example.server.log.entity.OperationLog;
import org.example.server.log.mapper.SysLogMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {
    private final SysLogMapper logMapper;
    private final ExecutorService POOL = Executors.newCachedThreadPool();
    private final List<BizLogHandler> handlers;

    @Around("@annotation(org.example.server.log.annotation.OperationLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标方法的注解和相关信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> classType = signature.getDeclaringType();
        Object[] args = joinPoint.getArgs();
        Object result;
        List<OperationLog> logs = handlers.stream().filter(i -> i.supportsMethod(classType, method)).map(handler -> {
            var logInfo = handler.convertToLog(classType, method, args);
            if (logInfo == null) {
                return null;
            }
            Long operatorId = handler.operatorId();
            String operator = handler.operator();
            String ip = handler.ip();
            // 构建日志实体类
            OperationLog sysLog = new OperationLog();
            sysLog.setMessage(logInfo.getMessage());
            sysLog.setOperator(operator);
            sysLog.setOperatorId(operatorId);
            sysLog.setIp(ip);
            sysLog.setLogType(logInfo.getType());
            sysLog.setName(method.getName());
            sysLog.setLevel("");
            sysLog.setOperateTime(System.currentTimeMillis());
            sysLog.setOldValue(logInfo.getOldValue());
            sysLog.setNewValue(logInfo.getNewValue());
            return sysLog;
        }).filter(Objects::nonNull).toList();
        result = joinPoint.proceed(); // 执行目标方法
        POOL.submit(() -> {
            logMapper.insert(logs);
        });
        return result;
    }
}
