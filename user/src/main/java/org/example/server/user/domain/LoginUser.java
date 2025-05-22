package org.example.server.user.domain;

import org.example.server.web.domain.BaseLoginUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginUser extends BaseLoginUser {
    /**
     * 用户名
     */
    private String username;
    /**
     * 头像链接
     */
    private String avatarImgUrl;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 名称
     */
    private String nickname;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 状态 1-可用，0-不可用
     */
    private Integer state;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 是否管理员
     * @return 布尔值
     */
    public boolean isAdmin() {
        return getRoles().contains("ADMIN");
    }
}
