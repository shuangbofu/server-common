package org.example.server.web.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.example.server.web.annotation.ResultBody;
import org.example.server.web.domain.LoginRequest;
import org.example.server.web.domain.LoginSuccess;
import org.example.server.web.domain.BaseLoginUser;
import org.example.server.web.service.CodeVerifyService;
import org.example.server.web.service.LoginService;
import org.example.server.web.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.server.web.utils.LoginUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 登录&登出
 */
@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("api")
public class LoginController {
    private final LoginService<? extends BaseLoginUser> loginService;
    private final PermissionService permissionService;
    private final CodeVerifyService codeVerifyService;

    /**
     * 登录
     * @param request
     * @return
     */
    @PostMapping("user/doLogin")
    @ResultBody
    public LoginSuccess<BaseLoginUser> doLogin(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        BaseLoginUser loginUser = loginService.loginCheck(request);
        String loginId = loginUser.getLoginId();
        log.info("用户[{},{}]登录", loginId, username);
        StpUtil.login(loginId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        LoginUtils.setUser(loginUser);
        loginService.afterLogin(loginId);
        return new LoginSuccess<>(loginUser, tokenInfo);
    }

    @PostMapping("user/sendLoginCode")
    @ResultBody
    public void sendCode(@RequestBody LoginRequest request) {
        loginService.sendCode(request);
    }

    /**
     * 登出
     */
    @PostMapping("user/doLogout")
    @ResultBody
    public void doLogout() {
        LoginUtils.checkLogin();
        String loginId = StpUtil.getLoginIdAsString();
        LoginUtils.setUser(loginService.getLoginUser(loginId));
        loginService.beforeLogout(loginId);
        StpUtil.logout();
    }

    /**
     * 获取登录用户信息
     * @return 信息
     */
    @GetMapping("user/info")
    @ResultBody
    public BaseLoginUser loginInfo() {
        LoginUtils.checkLogin();
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        BaseLoginUser user = loginService.getLoginUser(LoginUtils.getLoginId());
        user.setTokenInfo(tokenInfo);
        return user;
    }
}
