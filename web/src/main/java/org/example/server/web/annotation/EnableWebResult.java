package org.example.server.web.annotation;

import org.example.server.web.ResultBodyAdvice;
import org.example.server.web.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ResultBodyAdvice.class, GlobalExceptionHandler.class})
public @interface EnableWebResult {
}