package org.example.server.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.user.domain.param.RoleParam;
import org.example.server.user.domain.vo.RoleVO;
import org.example.server.user.service.RoleService;
import org.example.server.web.annotation.ResultController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理
 */
@ResultController("/api/sys/role")
@RequiredArgsConstructor
@Controller
public class RoleController {
    private final RoleService roleService;

    /**
     * 列表
     * @return 列表
     */
    @GetMapping("list")
    public List<RoleVO> roleList() {
        return roleService.roleList();
    }

    /**
     * 修改角色
     * @param param 参数
     */
    @PutMapping
    public void editRole(@Validated @RequestBody RoleParam param) {
        roleService.editRole(param);
    }

    /**
     * 创建角色
     * @param param 表单
     * @return 角色对象
     */
    @PostMapping
    public RoleVO createRole(@Validated @RequestBody RoleParam param) {
        return roleService.createRole(param);
    }

    /**
     * 删除角色
     * @param roleId ID
     */
    @DeleteMapping("{roleId}")
    public void removeRole(@PathVariable("roleId") Long roleId) {
        roleService.removeRole(roleId);
    }
}
