package org.example.server.user.domain.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPwdForm implements Serializable {
    /**
     * 旧密码
     */
    private String oldPwd;
    /**
     * 新密码
     */
    private String newPwd;
}
