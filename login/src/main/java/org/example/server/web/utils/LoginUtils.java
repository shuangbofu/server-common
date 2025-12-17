package org.example.server.web.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.server.web.ErrorCode;
import org.example.server.web.domain.BaseLoginUser;
import org.example.server.web.exception.BizException;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public class LoginUtils {
    private static final ThreadLocal<String> ID = new ThreadLocal<>();
    private static final ThreadLocal<Object> USER = new ThreadLocal<>();

    public static <T extends BaseLoginUser> void setUser(T loginUser) {
        ID.set(loginUser.getLoginId());
        USER.set(loginUser);
    }

    public static void clearId() {
        ID.remove();
    }

    public static <T extends BaseLoginUser> Optional<T> getUser() {
        return Optional.ofNullable(get(() -> (T) USER.get()));
    }

    public static String getNickname() {
        return get(() -> getUser().map(BaseLoginUser::getNickname).orElse(null));
    }

    public static String getLoginId() {
        return ID.get();
    }

    public static Long getUserId() {
        return get(() -> getUser().map(BaseLoginUser::getLoginId)
                .map(Long::parseLong).orElse(null));
    }

    public static void checkLogin() {
        try {
            StpUtil.checkLogin();
        } catch (NotLoginException e) {
            throw new BizException(ErrorCode.define(101, "账号未登录"));
        }
    }

    public static <T> T get(Supplier<T> supplier) {
        if(ID.get()==null) {
            return null;
        }
        return supplier.get();
    }

    public static String getIp() {
        return get(() -> {
            // 获取客户端 IP
            var request = (HttpServletRequest) SaHolder.getRequest().getSource();
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        });
    }

    public static boolean isMobileDevice() {
        return Optional.ofNullable(get(() -> {
            var MOBILE_KEYWORDS = List.of("Mobile", "Android", "iPhone",
                    "iPad", "iPod", "Windows Phone", "Mobi");
            var request = (HttpServletRequest) SaHolder.getRequest().getSource();
            String userAgent = request.getHeader("User-Agent");
            // 判断 User-Agent 是否包含任一移动端关键词
            return userAgent != null && MOBILE_KEYWORDS.stream().anyMatch(userAgent::contains);
        })).orElse(false);
    }
}
