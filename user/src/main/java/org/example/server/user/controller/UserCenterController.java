package org.example.server.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.user.domain.param.SelfUserForm;
import org.example.server.user.domain.param.UserPwdForm;
import org.example.server.user.service.UserService;
import org.example.server.web.annotation.ResultController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户-个人中心
 */
@ResultController(value = "/api/user",name = "userController")
@RequiredArgsConstructor
@Controller
public class UserCenterController {
    private final UserService userService;

    /**
     * 修改密码
     * @param userPwdForm
     */
    @PutMapping("/pwd")
    public void editPwd(@Validated @RequestBody UserPwdForm userPwdForm) {
        userService.editPwd(userPwdForm);
    }

    /**
     * 编辑用户信息
     * @param userForm
     */
    @PutMapping
    public void editSelfUser(@Validated @RequestBody SelfUserForm userForm) {
        userService.editSelfUser(userForm);
    }
}
