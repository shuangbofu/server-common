package org.example.server.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.user.domain.param.UserForm;
import org.example.server.user.domain.param.UserPageSearchFilter;
import org.example.server.user.domain.vo.UserVO;
import org.example.server.user.service.UserService;
import org.example.server.web.annotation.ResultController;
import org.example.server.web.domain.request.PageRequest;
import org.example.server.web.domain.request.PageVO;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 */
@ResultController(value = "/api/sys/user", name = "sysUserController")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    /**
     * 重置密码
     * @param userId
     */
    @PutMapping("pwd")
    public void resetPwd(@RequestParam("userId") Long userId) {
        userService.resetPwd(userId);
    }

    /**
     * 删除用户
     * @param userId
     */
    @DeleteMapping("{userId}")
    public void removeUser(@PathVariable("userId") Long userId) {
        userService.removeUser(userId);
    }

    /**
     * 编辑用户
     * @param form
     */
    @PutMapping
    public void editUser(@Validated @RequestBody UserForm form) {
        userService.editUser(form);
    }

    /**
     * 创建用户
     * @param form
     * @return
     */
    @PostMapping
    public UserVO createUser(@Validated @RequestBody UserForm form) {
        return userService.createUser(form);
    }

    /**
     * 用户分页
     * @param request
     * @return
     */
    @PostMapping("page")
    public PageVO<UserVO> userPage(@RequestBody PageRequest<UserPageSearchFilter> request) {
        return userService.userPage(request);
    }
}
