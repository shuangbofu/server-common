package org.example.server.user.annotation;

import org.example.server.user.config.GlobalConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GlobalConfig.class)
public @interface EnableUserModule {
}
