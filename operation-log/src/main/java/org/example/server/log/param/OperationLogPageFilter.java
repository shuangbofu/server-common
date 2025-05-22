package org.example.server.log.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperationLogPageFilter implements Serializable {
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 操作人ID
     */
    private Long operatorId;
    /**
     * 操作人IP
     */
    private String ip;
    /**
     * 内容
     */
    private String message;
    /**
     * 范围查询-开始时间
     */
    private Long startTime;
    /**
     * 范围查询-结束时间
     */
    private Long endTime;
    /**
     * 排序 true=desc，false=asc
     */
    private Boolean desc;
}
