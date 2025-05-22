package org.example.server.user.service;

import lombok.RequiredArgsConstructor;
import org.example.server.log.BizLogHandler;
import org.example.server.log.domain.LogInfo;
import org.example.server.common.utils.ObjectUtils;
import org.example.server.user.domain.param.RoleParam;
import org.example.server.user.domain.param.SelfUserForm;
import org.example.server.user.domain.param.UserForm;
import org.example.server.user.persistence.entity.UserRole;
import org.example.server.user.persistence.mapper.RoleMapper;
import org.example.server.user.persistence.mapper.UserMapper;
import org.example.server.user.persistence.mapper.UserRoleMapper;
import org.example.server.web.utils.LoginUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

import static org.example.server.web.utils.BeanUtils.aToB;

@Service
@RequiredArgsConstructor
public class UserLogHandlerImpl implements BizLogHandler {
    private final RoleMapper roleMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public LogInfo convertToLog(Class<?> classType, Method method, Object[] args) {
        if(classType.equals(UserServiceImpl.class)) {
            return switch (method.getName()) {
                case "beforeLogout" -> new LogInfo("登出", args[0]);
                case "afterLogin" -> new LogInfo("登录", args[0]);
                case "createUser" -> {
                    UserForm userForm = argsToObject(args, UserForm.class);
                    yield new LogInfo("创建用户", userForm.getNickname(), userForm);
                }
                case "editUser" -> {
                    UserForm userForm = argsToObject(args, UserForm.class);
                    List<Long> roles = userRoleMapper.selectValueList(UserRole::getRoleId, i -> i.eq(UserRole::getUserId, userForm.getId()));
                    UserForm old = aToB(userMapper.selectById(userForm.getId()), UserForm.class);
                    old.setRoleIds(roles);
                    yield new LogInfo("修改用户",
                            String.join(",", ObjectUtils.compareFields(old, userForm)), old, userForm);
                }
                case "editSelfUser" -> {
                    SelfUserForm form = argsToObject(args, SelfUserForm.class);
                    SelfUserForm old = aToB(userMapper.selectById(LoginUtils.getUserId()), SelfUserForm.class);
                    yield new LogInfo("个人编辑",
                            String.join(",", ObjectUtils.compareFields(old, form)), old, form);
                }
                case "removeUser" -> new LogInfo("删除用户", getNickname(args), args[0]);
                case "editPwd" -> new LogInfo("修改密码", null);
                case "resetPwd" -> new LogInfo("重置密码", getNickname(args), args[0]);
                default -> notSupportMethod(method);
            };
        } else if(classType.equals(RoleServiceImpl.class)) {
            return switch (method.getName()) {
                case "removeRole" -> new LogInfo("删除角色", roleMapper.getName((Long) args[0]), args[0]);
                case "editRole" -> {
                    RoleParam param = argsToObject(args, RoleParam.class);
                    RoleParam old = aToB(roleMapper.selectById(param.getId()), RoleParam.class);
                    yield new LogInfo("编辑角色",
                            String.join(",", ObjectUtils.compareFields(old, param)), old, param);
                }
                case "createRole" -> new LogInfo("创建角色", args[0]);
                default -> notSupportMethod(method);
            };
        } else {
            return notSupportMethod(method);
        }
    }

    private String getNickname(Object[] args) {
        return userService.getNickname(argsToObject(args, Long.class));
    }

    @Override
    public boolean supportsMethod(Class<?> classType, Method method) {
        return classType.getName()
                .startsWith("org.example.server.user");
    }
}
