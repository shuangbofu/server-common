package org.example.server.user.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserVO implements Serializable {
    /**
     * ID
     */
    private Long id;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 用户名
     */
    private String username;
    /**
     * 头像图片链接
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
     * 创建时间
     */
    private Long createTime;
    /**
     * 修改时间
     */
    private Long updateTime;
    /**
     * 权限ID列表
     */
    private List<RoleSimpleVO> roles;
}
