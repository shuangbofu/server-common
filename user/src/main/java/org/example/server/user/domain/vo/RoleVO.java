package org.example.server.user.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleVO extends RoleSimpleVO {
    /**
     * 用户列表
     */
    private List<UserVO> userList = new ArrayList<>();
    /**
     * 菜单列表
     */
    private List<MenuVO> menusList = new ArrayList<>();
}
