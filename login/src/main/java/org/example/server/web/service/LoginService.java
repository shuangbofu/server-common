package org.example.server.web.service;

import org.example.server.web.domain.BaseLoginUser;
import org.example.server.web.domain.LoginRequest;

public interface LoginService<T extends BaseLoginUser> {


    void sendCode(LoginRequest loginRequest);

    /**
     * 登录检查
     * @param request 登录请求对象
     * @return 用户对象
     */
    T loginCheck(LoginRequest request);

    /**
     * 获取登录用户
     * @param loginId 登录ID
     * @return 用户对象
     */
    T getLoginUser(String loginId);

    /**
     * 登录后回调
     * @param loginId 登录ID
     */
    default void afterLogin(String loginId) {}

    /**
     * 注销前回调
     * @param loginId 登录ID
     */
    default void beforeLogout(String loginId) {}
}
