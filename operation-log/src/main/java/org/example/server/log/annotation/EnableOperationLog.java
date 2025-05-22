package org.example.server.log.annotation;

import org.example.server.log.aspect.OperationLogAspect;
import org.example.server.log.config.OperationLogConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({OperationLogConfig.class})  // 引入配置类
public @interface EnableOperationLog {
}