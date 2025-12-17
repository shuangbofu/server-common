package org.example.server.user.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.example.server.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("`user`")
public class User extends BaseEntity<User> implements Serializable {
    private String username;
    private String password;
    private String avatarImgUrl;
    private String email;
    private String mobile;
    private String nickname;
    private Integer gender;
    private Integer state;
}
