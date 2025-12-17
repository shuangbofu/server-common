package org.example.server.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.server.log.annotation.OperationLog;
import org.example.server.user.constant.ErrorCodes;
import org.example.server.user.domain.param.RoleParam;
import org.example.server.user.domain.vo.MenuVO;
import org.example.server.user.domain.vo.RoleVO;
import org.example.server.user.domain.vo.UserVO;
import org.example.server.user.persistence.entity.Role;
import org.example.server.user.persistence.entity.RoleMenu;
import org.example.server.user.persistence.entity.UserRole;
import org.example.server.user.persistence.mapper.*;
import org.example.server.mybatis.IdEntity;
import lombok.RequiredArgsConstructor;
import org.example.server.web.ErrorCode;
import org.example.server.web.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.example.server.web.utils.BeanUtils.*;
import static org.example.server.web.utils.CommonUtils.notEmpty;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public List<RoleVO> roleList() {
        return roleVOList(roleMapper.selectAll());
    }

    private List<RoleVO> roleVOList(List<Role> roles) {
        List<Long> roleIds = roles.stream().map(IdEntity::getId).toList();

        // 查询关联菜单ID集合
        Map<Long, List<Long>> menuIdMaps =
                roleMenuMapper.selectBListInAGroupBy(RoleMenu::getRoleId, roleIds, RoleMenu::getMenuId);

        // 查询关联用户ID集合
        Map<Long, List<Long>> userIdMaps =
                userRoleMapper.selectBListInAGroupBy(UserRole::getRoleId, roleIds, UserRole::getUserId);

        return listAToListB(roles, RoleVO.class, (a,b) -> {
            b.setMenusList(listAToListB(menuMapper.selectListInIds(menuIdMaps.get(a.getId())), MenuVO.class));
            b.setUserList(listAToListB(userMapper.selectListInIds(userIdMaps.get(a.getId())), UserVO.class));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editRole(RoleParam param) {
        Role role = aToB(param, Role.class);
        checkName(role);
        // 更新角色数据
        roleMapper.updateById(role);
        upsertRoleRelations(param.getId(), param.getUserIds(), param.getMenuIds());
    }

    private void checkName(Role role) {
        roleMapper.checkUniqueThrow(role, Role::getName,
                () -> new BizException(ErrorCodes.ROLE_NAME_DUPLICATE, role.getName()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void upsertRoleRelations(Long roleId, List<Long> userIds, List<Long> menuIds) {
        // 更新菜单关联关系
        removeRoleMenu(roleId, null);
        notEmpty(menuIds, list -> {
            List<RoleMenu> roleMenus = list.stream()
                    .map(i -> new RoleMenu(roleId, i)).toList();
            roleMenuMapper.insert(roleMenus);
        });

        // 更新用户关联关系
        removeUserRole(null, roleId);
        notEmpty(userIds, list -> {
            userRoleMapper.insert(list.stream().map(i->new UserRole(i, roleId)).toList());
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO createRole(RoleParam param) {
        Role role = aToBIgnoreId(param, Role.class);
        checkName(role);
        role = roleMapper.insertRt(role);
        upsertRoleRelations(role.getId(), param.getUserIds(), param.getMenuIds());
        return roleVOList(List.of(role)).get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationLog
    public void removeRole(Long roleId) {
        // 删除角色
        roleMapper.deleteById(roleId);
        // 清除用户关联
        removeUserRole(null, roleId);
        // 清除菜单关联
        removeRoleMenu(roleId, null);
    }

    @Override
    public void removeUserRole(Long userId, Long roleId) {
        if(userId!=null) {
            userRoleMapper.deleteBy(q -> q.eq(UserRole::getUserId, userId));
        }
        if(roleId!=null) {
            userRoleMapper.deleteBy(q->q.eq(UserRole::getRoleId, roleId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(Long userId, List<Long> roleIds) {
        removeUserRole(userId, null);
        if(roleIds!=null && userId!=null) {
            userRoleMapper.insert(roleIds.stream().map(i->new UserRole(userId, i)).toList());
        }
    }

    @Override
    public void removeRoleMenu(Long roleId, Long menuId) {
        if(roleId!=null) {
            roleMenuMapper.deleteBy(q->q.eq(RoleMenu::getRoleId, roleId));
        }
        if(menuId!=null) {
            roleMenuMapper.deleteBy(q->q.eq(RoleMenu::getMenuId, menuId));
        }
    }
}
