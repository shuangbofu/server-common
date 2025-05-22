package org.example.server.user.service;

import org.example.server.user.domain.param.MenuForm;
import org.example.server.user.domain.vo.MenuVO;
import org.example.server.user.persistence.entity.Menu;
import org.example.server.user.persistence.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.server.web.utils.BeanUtils.*;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuMapper menuMapper;
    private final RoleService roleService;

    @Override
    public List<MenuVO> menuList() {
        return listAToListB(menuMapper.selectAll(),MenuVO.class);
    }

    @Override
    public void editMenu(MenuForm form) {
        menuMapper.updateById(aToB(form, Menu.class));
    }

    @Override
    public MenuVO createMenu(MenuForm form) {
        return aToB(menuMapper.insertRt(aToBIgnoreId(form, Menu.class)), MenuVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMenu(Long menuId) {
        menuMapper.deleteById(menuId);
        roleService.removeRoleMenu(null, menuId);
    }
}
