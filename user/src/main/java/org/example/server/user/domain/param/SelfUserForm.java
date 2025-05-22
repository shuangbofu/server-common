package org.example.server.user.domain.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.server.common.annotation.DisplayName;

import java.io.Serializable;

@Data
public class SelfUserForm implements Serializable {
    /**
     * 头像图片链接
     */
    @DisplayName("头像")
    private String avatarImgUrl;
    /**
     * 邮箱
     */
    @Email
    @DisplayName("邮箱")
    private String email;
    /**
     * 名称
     */
    @NotNull
    @DisplayName("名称")
    private String nickname;
    /**
     * 手机号
     */
    @NotNull
    @DisplayName("手机号")
    private String mobile;
}
