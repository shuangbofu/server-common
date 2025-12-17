package org.example.server.user.domain.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MenuForm implements Serializable {
    /**
     * ID
     */
    private Long id;
    /**
     * 名称
     */
    @NotEmpty
    private String name;
    /**
     * 父ID
     */
    @NotNull
    private Long pid;
    /**
     * 属性 JSON
     */
    private Map<String,Object> props;
}
