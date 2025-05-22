package org.example.server.web.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.PostConstruct;
import org.example.server.web.ErrorCode;
import org.example.server.web.annotation.TokenAccessible;
import org.example.server.web.domain.BaseLoginUser;
import org.example.server.web.domain.RouterPermission;
import org.example.server.web.exception.BizException;
import org.example.server.web.interfaces.TokenAccessHandler;
import org.example.server.web.service.PermissionService;
import org.example.server.web.service.StpInterfaceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.server.web.utils.LoginUtils;
import org.springframework.context.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Objects;

/**
 * Sa-Token 配置类
 */
@Slf4j
@Configuration("saTokenConfigure")
@RequiredArgsConstructor
@Import(StpInterfaceImpl.class)
public class SaTokenConfigure implements WebMvcConfigurer {
    private final LoginModuleContext context;

    @PostConstruct
    public void init() {
        log.info("开启账号登录");
    }

    private String getFullPath() {
        String requestPath = SaHolder.getRequest().getRequestPath();
        String currentRequestMethod = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getMethod();
        // 构建当前请求的完整标识
        return currentRequestMethod + " " + requestPath;
    }
    public void checkLogin(Object handle) {
        SaRequest request = SaHolder.getRequest();
        request.getParam("");
        try {
            LoginUtils.checkLogin();
        } catch (Exception e) {
            boolean continute = false;
            if (context.isTokenAccessEnable() && handle instanceof HandlerMethod handlerMethod) {
                boolean tokenAccessible = !context.isNeedAnnotation() || handlerMethod.hasMethodAnnotation(TokenAccessible.class);
                TokenAccessHandler<?> tokenAccessHandler = context.getTokenAccessHandler();
                if (tokenAccessHandler != null && tokenAccessible) {
                    Object access = tokenAccessHandler.handleAccess(handlerMethod, request);
                    if(tokenAccessHandler.login()) {
                        if(access!=null) {
                            StpUtil.login(access);
                            continute = true;
                        }
                    }
                    if(access instanceof Boolean) {
                        if((Boolean) access) {
                            return;
                        }
                    }
                }
            }
            log.warn("拦截到请求: {}", getFullPath(),e); // 打印请求的 URL
            if(!continute) {
                throw e;
            }
        }
        String loginId = StpUtil.getLoginIdAsString();
        BaseLoginUser user = context.getLoginService().getLoginUser(loginId);
        LoginUtils.setUser(user);
        PermissionService permissionService = context.getPermissionService();
        List<RouterPermission> routerPermissions = permissionService.getRouterPermissions();
        // 动态路由权限
        routerPermissions.forEach(p -> {
            String[] permissions = p.getPermissions().toArray(new String[0]);
            String[] roles = p.getRoles().toArray(new String[0]);
            SaRouter.match(p.getPathPattern(), r-> {
                try {
                    if (p.getAndOrCondition()) {
                        if(permissions.length > 0) {
                            StpUtil.checkPermissionAnd(permissions);
                        }
                        if(roles.length > 0) {
                            StpUtil.checkRoleAnd(roles);
                        }
                    } else {
                        if(permissions.length > 0) {
                            StpUtil.checkPermissionOr(permissions);
                        }
                        if(roles.length > 0) {
                            StpUtil.checkRoleOr(roles);
                        }
                    }
                } catch (NotPermissionException e) {
                    log.warn("用户[{}]没有权限{}, 缺少权限码{}",loginId, p.getName(), e.getPermission(),e);
                    throw new BizException(ErrorCode.define(102, "没有「%s」权限"), p.getName());
                }
            });
        });
    }

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(this::checkLogin))
        .addPathPatterns("/**").excludePathPatterns("/api/user/doLogin","/api/user/doLogout")
                .excludePathPatterns("/", "/index", "/**.js", "/**.css", "/**.html",
                        "/static/**","/public/**","/assets/**", "/favicon.ico");
    }
}
