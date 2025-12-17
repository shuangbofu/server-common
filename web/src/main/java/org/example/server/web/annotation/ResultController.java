package org.example.server.web.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Controller
@ResultBody
@RequestMapping
public @interface ResultController {
    @AliasFor(
        annotation = RequestMapping.class,
        attribute = "path"
    )
    String[] value() default {};

    @AliasFor(
            annotation = Controller.class,
            attribute = "value"
    )
    String name() default "";
}