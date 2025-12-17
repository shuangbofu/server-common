package org.example.server.user.domain.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageSearchFilter implements Serializable {
    /**
     * 搜索文本
     */
    private String searchText;
    /**
     * 可用状态 1-可用，0-不可用
     */
    private Integer state;
}
