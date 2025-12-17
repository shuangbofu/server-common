package org.example.server.web.config;

import org.example.server.web.domain.BaseLoginUser;
import org.example.server.web.interfaces.TokenAccessHandler;
import org.example.server.web.service.LoginService;
import org.example.server.web.service.PermissionService;
import lombok.Getter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginModuleContext implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext applicationContext;
    @Getter
    private LoginService<? extends BaseLoginUser> loginService;
    @Getter
    private PermissionService permissionService;

    @Value("${login.token-access.enable:false }")
    @Getter
    private boolean tokenAccessEnable;

    @Value("${login.token-access.annotation:true}")
    @Getter
    private boolean needAnnotation;

    @Getter
    private TokenAccessHandler<?> tokenAccessHandler;

    // 设置 ApplicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> T getService(Class<T> tClass) {
        try {
            return applicationContext.getBean(tClass);
        } catch (Exception e) {
            // 如果没有找到 LoginService 的实现，抛出异常并提示用户
            throw new IllegalStateException("请实现 "+tClass.getName()+" 接口注册到spring中");
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 获取 LoginService 的实现
        permissionService = getService(PermissionService.class);
        loginService = getService(LoginService.class);
        if(tokenAccessEnable) {
            tokenAccessHandler = getService(TokenAccessHandler.class);
        }
    }
}