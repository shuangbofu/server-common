package org.example.server.log.vo;

import lombok.Data;
import org.example.server.common.annotation.DisplayName;

@Data
public class OperationLogExcelItem  {
    private Long id;
    /**
     * 日志类型
     */
    @DisplayName("操作类型")
    private String logType;
    /**
     * 日志内容
     */
    @DisplayName("操作详情")
    private String message;
    /**
     * 操作人名称
     */
    @DisplayName("操作用户")
    private String operator;
    /**
     * 操作人ID
     */
    private Long operatorId;
    /**
     * 操作人IP
     */
    @DisplayName("IP地址")
    private String ip;
    /**
     * 日志等级
     */
    private String level;
    /**
     * 操作时间
     */
    @DisplayName("操作时间")
    private String operateTime;
    /**
     * 名称
     */
    private String name;
}
