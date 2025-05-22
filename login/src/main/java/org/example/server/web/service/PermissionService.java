package org.example.server.web.service;

import org.example.server.web.domain.RouterPermission;

import java.util.List;

public interface PermissionService {

    /**
     * 请求路由权限
     * @return 权限集合
     */
    default List<RouterPermission> getRouterPermissions() {
        return List.of();
    }

    /**
     * 根据登录ID获取权限码
     * @param loginId 登录ID
     * @return 权限码集合
     */
    List<String> getPermissions(String loginId);

    /**
     * 根据登录ID获取角色码
     * @param loginId 登录ID
     * @return 角色码
     */
    List<String> getRoles(String loginId);
}
