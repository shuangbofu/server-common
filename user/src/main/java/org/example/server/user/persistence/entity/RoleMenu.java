package org.example.server.user.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.example.server.mybatis.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("`role_menu`")
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu extends IdEntity<RoleMenu> {
    private Long roleId;
    private Long menuId;
}
