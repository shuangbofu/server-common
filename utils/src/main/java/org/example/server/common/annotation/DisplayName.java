package org.example.server.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)  // 目标是字段
@Retention(RetentionPolicy.RUNTIME)  // 保留到运行时
public @interface DisplayName {
    String value();  // 字段的中文描述
}