package org.example.server.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({MybatisPlusConfig.class})
public @interface EnableMybatis {

}