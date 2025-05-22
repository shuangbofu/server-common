package org.example.server.user.domain.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.example.server.common.annotation.DisplayName;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleParam implements Serializable {
    /**
     * 用户ID集合
     */
    @DisplayName("用户集合")
    private List<Long> userIds;
    /**
     * 菜单集合
     */
    @DisplayName("菜单集合")
    private List<Long> menuIds;
    /**
     * 角色ID
     */
    private Long id;
    /**
     * 角色名称
     */
    @DisplayName("名称")
//    @NotEmpty
    private String name;
}
