package org.example.server.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.user.domain.param.MenuForm;
import org.example.server.user.domain.vo.MenuVO;
import org.example.server.user.service.MenuService;
import org.example.server.web.annotation.ResultController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理
 */
@ResultController("/api/sys/menu")
@RequiredArgsConstructor
@Controller
public class MenuController {
    private final MenuService menuService;

    /**
     * 菜单列表
     * @return 列表
     */
    @GetMapping("list")
    public List<MenuVO> menuList() {
        return menuService.menuList();
    }

    /**
     * 编辑菜单
     * @param form 表单
     */
    @PutMapping
    public void editMenu(@Validated @RequestBody MenuForm form) {
        menuService.editMenu(form);
    }

    /**
     * 创建菜单
     * @param form 表单
     * @return 菜单对象
     */
    @PostMapping
    public MenuVO createMenu(@Validated @RequestBody MenuForm form) {
        return menuService.createMenu(form);
    }

    /**
     * 删除菜单
     * @param menuId ID
     */
    @DeleteMapping("{menuId}")
    public void removeMenu(@PathVariable("menuId") Long menuId) {
        menuService.removeMenu(menuId);
    }
}
