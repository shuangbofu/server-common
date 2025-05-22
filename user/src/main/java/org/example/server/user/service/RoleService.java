package org.example.server.user.service;

import org.example.server.user.domain.param.RoleParam;
import org.example.server.user.domain.vo.RoleVO;

import java.util.List;

public interface RoleService {

    List<RoleVO> roleList();

    void editRole(RoleParam param);

    RoleVO createRole(RoleParam param);

    void removeRole(Long id);

    void removeUserRole(Long userId, Long roleId);

    void updateUserRole(Long userId, List<Long> roleIds);

    void removeRoleMenu(Long roleId, Long menuId);
}
