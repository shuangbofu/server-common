package org.example.server.web.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class RouterPermission implements Serializable {
    /**
     * 路径pattern
     */
    private String pathPattern;
    private String name;
    private List<String> permissions;
    private List<String> roles;
    /**
     * 且/或
     * true=且，false=或
     */
    private Boolean andOrCondition;
}
