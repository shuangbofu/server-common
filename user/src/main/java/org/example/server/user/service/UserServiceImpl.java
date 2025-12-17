package org.example.server.user.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.example.server.log.annotation.OperationLog;
import org.example.server.mybatis.IdEntity;
import org.example.server.user.config.GlobalConfig;
import org.example.server.user.domain.LoginUser;
import org.example.server.user.domain.param.SelfUserForm;
import org.example.server.user.domain.param.UserForm;
import org.example.server.user.domain.param.UserPageSearchFilter;
import org.example.server.user.domain.param.UserPwdForm;
import org.example.server.user.domain.vo.RoleSimpleVO;
import org.example.server.user.domain.vo.UserVO;
import org.example.server.user.persistence.entity.Role;
import org.example.server.user.persistence.entity.User;
import org.example.server.user.persistence.entity.UserRole;
import org.example.server.user.persistence.mapper.RoleMapper;
import org.example.server.user.persistence.mapper.UserMapper;
import org.example.server.user.persistence.mapper.UserRoleMapper;
import org.example.server.web.domain.LoginRequest;
import org.example.server.web.domain.RouterPermission;
import org.example.server.web.domain.request.PageRequest;
import org.example.server.web.domain.request.PageVO;
import org.example.server.web.exception.BizException;
import org.example.server.web.service.CodeVerifyService;
import org.example.server.web.service.LoginService;
import org.example.server.web.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.example.server.web.utils.LoginUtils;
import org.example.server.user.utils.UserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.server.user.constant.ErrorCodes.*;
import static org.example.server.web.utils.BeanUtils.*;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements LoginService<LoginUser>, PermissionService, UserService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final GlobalConfig globalConfig;
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final CodeVerifyService codeVerifyService;

    @Value("${login.mobile-login:false }")
    @Getter
    private boolean mobileLogin;

    @PostConstruct
    public void updateUserNicknameCache() {
        UserUtils.updateCache(userMapper.selectList(i->i.select(User::getId, User::getNickname))
                .stream().collect(Collectors.toMap(i->i.getId().toString(), User::getNickname)));
    }

    @Override
    public void sendCode(LoginRequest request) {
        User user = userMapper.getUserByMobile(request.getMobile());
        if(user == null) {
            throw new BizException(USER_NOT_FOUND);
        }
        codeVerifyService.sendCode(request.getMobile(),"LOGIN");
    }

    @Override
    public LoginUser loginCheck(LoginRequest request) {
        User user;
        if(!mobileLogin) {
            String username = request.getUsername();
            String password = request.getPassword();
            user = userMapper.selectOne(i -> i
                            .eq(User::getUsername, username).or()
                            .eq(User::getMobile, username),
                    () -> new BizException(USER_NOT_FOUND));
            boolean equals = SaSecureUtil.sha256(password).equals(user.getPassword());
            if (!equals) {
                throw new BizException(PASSWORD_WRONG, username);
            }
        } else {
            String mobile = request.getMobile();
            codeVerifyService.verifyCode("LOGIN", mobile, request.getCode());
            user = userMapper.getUserByMobile(request.getMobile());
        }
        if (user == null) {
            throw new BizException(USER_NOT_FOUND);
        }
        if (user.getState() == 0) {
            throw new BizException(USER_INVALID);
        }
        Long id = user.getId();
        return getLoginUser(id.toString());
    }

    @Override
    public LoginUser getLoginUser(String loginId) {
        User user = userMapper.selectById(loginId);
        if(user == null) {
            throw new BizException(USER_NOT_FOUND);
        }
        LoginUser loginUser = aToB(user, LoginUser.class,
                (a, b) -> b.setLoginIdLong(user.getId()));
        loginUser.setRoles(getRoles(loginId).stream()
                .map(Long::parseLong).toList());
        return loginUser;
    }

    @Override
    @OperationLog
    public void afterLogin(String loginId) {
//        LoginUtils.setUser(getLoginUser(loginId));
    }

    @Override
    @OperationLog
    public void beforeLogout(String loginId) {
//        LoginUtils.checkLogin();
    }

    @Override
    public List<String> getPermissions(String loginId) {
        return List.of();
    }

    @Override
    public List<String> getRoles(String loginId) {
        return userRoleMapper.selectValueList(UserRole::getRoleId,
                i -> i.select(UserRole::getRoleId)
                .eq(UserRole::getUserId, loginId))
                .stream().map(Object::toString).toList();
    }

    public List<RouterPermission> getRouterPermissions() {
        return List.of();
    }

    @Override
    @OperationLog
    public void editPwd(UserPwdForm form) {
        Long userId = LoginUtils.getUserId();
        User user = userMapper.selectOne(i->i.eq(User::getId, userId), ()-> new BizException(USER_NOT_FOUND));
        String password = user.getPassword();
        if(!password.equals(SaSecureUtil.sha256(form.getOldPwd()))) {
            throw new BizException(PASSWORD_WRONG, user.getUsername());
        }
        boolean success = userMapper.updateBy(i -> i.eq(User::getId, userId)
                .set(User::getPassword, SaSecureUtil.sha256(form.getNewPwd())));
        if(success) {
            StpUtil.logout();
        }
    }

    @Override
    @OperationLog
    public void resetPwd(Long userId) {
        boolean success = userMapper.updateById(userId,
                i -> i.set(User::getPassword,
                        globalConfig.getDefaultPwd()));
        if(success) {
            StpUtil.logout(userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationLog
    public void removeUser(Long userId) {
        roleService.removeUserRole(null, userId);
        int rows = userMapper.deleteById(userId);
        if(rows > 0) {
            StpUtil.logout(userId);
        }
    }

    @Override
    @OperationLog
    public void editSelfUser(SelfUserForm form) {
        LoginUtils.getUser().ifPresent(user -> {
            UserForm userForm = aToB(user, UserForm.class);
            copyAToB(form, userForm);
            userForm.setId(LoginUtils.getUserId());
            userForm.setRoleIds(user.getRoles());
            editUser(userForm);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationLog
    public void editUser(UserForm form) {
        checkUserDuplicates(form);
        userMapper.updateById(aToB(form, User.class));
        roleService.updateUserRole(form.getId(), form.getRoleIds());
        updateUserNicknameCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationLog
    public UserVO createUser(UserForm form) {
        checkUserDuplicates(form);
        UserVO user = aToB(userMapper.insertRt(aToBIgnoreId(form, User.class, (a, b) -> {
            // 默认密码
            b.setPassword(globalConfig.getDefaultPwd());
            if (a.getAvatarImgUrl() == null || a.getAvatarImgUrl().isEmpty()) {
                // 默认头像
                b.setAvatarImgUrl(globalConfig.getDefaultAvatarImgUrl());
            }
        })), UserVO.class);
        roleService.updateUserRole(user.getId(), form.getRoleIds());
        updateUserNicknameCache();
        return setupRole(user);
    }

    private void checkUserDuplicates(UserForm form) {
        if(StringUtils.isNotEmpty(form.getMobile()) &&
                userMapper.countBy(i -> i.ne(form.getId() != null, User::getId, form.getId())
                .eq(User::getMobile, form.getMobile())) > 0) {
            throw new BizException(MOBILE_DUPLICATE, form.getMobile());
        }
        if(StringUtils.isNotEmpty(form.getUsername()) &&
                userMapper.countBy(i-> i.ne(form.getId()!=null, User::getId, form.getId())
                .eq(User::getUsername ,form.getUsername())) >0 ) {
            throw new BizException(USERNAME_DUPLICATE, form.getUsername());
        }
    }

    @Override
    public PageVO<UserVO> userPage(PageRequest<UserPageSearchFilter> request) {
        UserPageSearchFilter filter = request.getFilter();
        Page<User> page = userMapper.selectPage(request, i ->
                i.and(StringUtils.isNotEmpty(filter.getSearchText()),
                                q -> q.like(User::getMobile, filter.getSearchText())
                                        .or().like(User::getNickname, filter.getSearchText()))
                        .eq(filter.getState() != null, User::getState, filter.getState())
                        .orderByDesc(User::getCreateTime));
       return PageVO.of(page, setupRoles(listAToListB(page.getRecords(),UserVO.class)));
    }

    private List<UserVO> setupRoles(List<UserVO> users) {
        List<Long> userIds = users.stream().map(UserVO::getId).toList();
        Map<Long, List<Long>> roleIdMap = userRoleMapper.selectBListInAGroupBy(UserRole::getUserId, userIds, UserRole::getRoleId);
        List<Long> roleIds = roleIdMap.values().stream().flatMap(Collection::stream).distinct().toList();
        Map<Long, Role> roleMap = roleMapper.selectListInIds(roleIds)
                .stream().collect(Collectors.toMap(IdEntity::getId, i -> i));
        return users.stream().peek(i-> {
            List<RoleSimpleVO> roles = roleIdMap.getOrDefault(i.getId(), new ArrayList<>())
                    .stream().map(j -> aToB(roleMap.get(j), RoleSimpleVO.class))
                    .toList();
            i.setRoles(roles);
        }).toList();
    }

    private UserVO setupRole(UserVO userVO) {
       return setupRoles(List.of(userVO)).get(0);
    }

    private UserVO setupRole(User user) {
        if(user==null) {
            throw new BizException(USER_NOT_FOUND);
        }
        return setupRoles(List.of(aToB(user, UserVO.class))).get(0);
    }

    @Override
    public UserVO getUser(Long userId) {
        return setupRole(userMapper.selectById(userId));
    }

    @Override
    public List<UserVO> getUsersInIds(List<Long> ids) {
        return setupRoles(listAToListB(userMapper.selectListInIds(ids), UserVO.class));
    }

    @Override
    public List<UserVO> getUsersLikeNickname(String nickname) {
        return setupRoles(listAToListB(userMapper.selectList(i->
                i.like(StringUtils.isNotEmpty(nickname), User::getNickname, nickname)), UserVO.class));
    }

    @Override
    public String getNickname(Long id) {
        return userMapper.selectValue(User::getNickname, i->i.eq(User::getId, id));
    }

    @Override
    public List<String> getNicknames(List<Long> ids) {
        if(ids.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.selectValueList(User::getNickname, i->i.in(User::getId,ids));
    }

    @Override
    public UserVO getUserByMobile(String mobile) {
        return setupRole(userMapper.selectOne(i->i.eq(User::getMobile, mobile)));
    }
}
