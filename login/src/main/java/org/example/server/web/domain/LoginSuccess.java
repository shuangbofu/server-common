package org.example.server.web.domain;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccess<T extends BaseLoginUser> implements Serializable {
    /**
     * 用户
     */
    private T user;
    /**
     * token信息
     */
    private SaTokenInfo tokenInfo;
}
