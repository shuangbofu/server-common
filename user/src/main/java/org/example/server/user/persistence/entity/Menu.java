package org.example.server.user.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.example.server.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "`menu`", autoResultMap = true)
public class Menu extends BaseEntity<Menu> {
    private String name;
    private Long pid;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> props;
}
