package org.example.server.user.domain.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.server.common.annotation.DisplayName;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserForm extends SelfUserForm implements Serializable {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    @NotNull
    @DisplayName("用户名")
    private String username;
    /**
     * 性别
     */
    @NotNull
    @DisplayName("性别")
    private Integer gender;
    /**
     * 状态 1-可用 0-不可用
     */
    @NotNull
    @DisplayName("状态")
    private Integer state;
    /**
     * 角色ID
     */
    @DisplayName("角色ID")
    private List<Long> roleIds;
}
