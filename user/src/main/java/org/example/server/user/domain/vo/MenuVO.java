package org.example.server.user.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MenuVO implements Serializable {
    /**
     * ID
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private Long pid;
    /**
     * 属性 JSON
     */
    private Map<String, Object> props;
}
