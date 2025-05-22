package org.example.server.user.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleSimpleVO implements Serializable {
    /**
     * ID
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
}
