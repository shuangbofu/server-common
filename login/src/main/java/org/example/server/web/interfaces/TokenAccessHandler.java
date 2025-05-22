package org.example.server.web.interfaces;

import cn.dev33.satoken.context.model.SaRequest;
import org.example.server.web.domain.BaseLoginUser;
import org.example.server.web.utils.LoginUtils;
import org.springframework.web.method.HandlerMethod;

public interface TokenAccessHandler<T> {

    default boolean login() {
        return false;
    }

    default T handleAccess(HandlerMethod handlerMethod, SaRequest request) {
        LoginUtils.setUser(new User());
        return null;
    }

    class User extends BaseLoginUser {
    }
}
