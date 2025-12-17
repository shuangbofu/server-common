package org.example.server.user.persistence.mapper;

import org.example.server.user.persistence.entity.User;
import org.example.server.mybatis.BaseMapper;

public interface UserMapper extends BaseMapper<User> {

    default User getUserByMobile(String mobile) {
        return selectOne(i -> i.eq(User::getMobile, mobile));
    }
}
