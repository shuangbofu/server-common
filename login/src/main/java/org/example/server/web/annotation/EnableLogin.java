package org.example.server.web.annotation;

import org.example.server.web.config.LoginModuleContext;
import org.example.server.web.config.SaTokenConfigure;
import org.example.server.web.controller.LoginController;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
//        SaTokenConfigRegister.class,
        SaTokenConfigure.class,
        LoginModuleContext.class,
        LoginController.class})
public @interface EnableLogin {

    // token 名称（同时也是 cookie 名称）
    String tokenName() default "user-token";

    // token 有效期（单位：秒），默认30天，-1代表永不过期
    long timeout() default 2592000;

    // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
    long activeTimeout() default -1;

    // 是否允许同一账号多地同时登录（为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
    boolean isConcurrent() default true;

    // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
    boolean isShare() default true;

    // token 风格
    String tokenStyle() default "tik";
}
