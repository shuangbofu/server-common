package org.example.server.user.service;

import org.example.server.user.domain.param.MenuForm;
import org.example.server.user.domain.vo.MenuVO;

import java.util.List;

public interface MenuService {

    List<MenuVO> menuList();

    void editMenu(MenuForm form);

    MenuVO createMenu(MenuForm form);

    void removeMenu(Long id);
}
