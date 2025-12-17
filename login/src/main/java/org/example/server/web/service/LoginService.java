package org.example.server.web.service;

import org.example.server.web.domain.BaseLoginUser;

public interface LoginService<T extends BaseLoginUser> {
    /**
     * 登录检查
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    T loginCheck(String username, String password);

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
