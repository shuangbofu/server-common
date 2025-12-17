package org.example.server.web.domain;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseLoginUser implements Serializable {
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录ID（用户ID）
     */
    private String loginId;
    /**
     * 登录名（用户名称）
     */
    private String nickname;
    /**
     * 角色ID列表
     */
    private List<Long> roles = new ArrayList<>();
    private SaTokenInfo tokenInfo;

    public void setLoginIdLong(Long id) {
        setLoginId(id.toString());
    }
}
