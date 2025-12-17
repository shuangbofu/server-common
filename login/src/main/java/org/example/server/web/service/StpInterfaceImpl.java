package org.example.server.web.service;

import cn.dev33.satoken.stp.StpInterface;
import org.example.server.web.config.LoginModuleContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    private final LoginModuleContext context;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return context.getPermissionService().getPermissions(loginId.toString());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return context.getPermissionService().getRoles(loginId.toString());
    }
}