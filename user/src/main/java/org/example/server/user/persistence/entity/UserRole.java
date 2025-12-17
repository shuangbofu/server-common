package org.example.server.user.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.example.server.mybatis.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("`user_role`")
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends IdEntity<UserRole> {
    private Long userId;
    private Long roleId;
}
