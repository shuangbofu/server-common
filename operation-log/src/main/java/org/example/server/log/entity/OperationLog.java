package org.example.server.log.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.server.mybatis.IdEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "operation_log", autoResultMap = true)
public class OperationLog extends IdEntity<OperationLog> {
    private String message;
    private String operator;
    private Long operatorId;
    private String ip;
    private String level;
    private Long operateTime;
    private String logType;
    private String name;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object oldValue;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object newValue;
}
