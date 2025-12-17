package org.example.server.user.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.example.server.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("`role`")
public class Role extends BaseEntity<Role> {
    private String name;
}
