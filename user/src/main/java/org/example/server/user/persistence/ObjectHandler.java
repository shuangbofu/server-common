package org.example.server.user.persistence;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.example.server.web.utils.LoginUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String loginId = LoginUtils.getLoginId();
        strictInsertFill(metaObject, "createTime", Long.class, System.currentTimeMillis());
        strictInsertFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
        if(loginId!=null) {
            strictInsertFill(metaObject, "createBy", String.class, loginId);
            strictInsertFill(metaObject, "updateBy", String.class, loginId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
        String loginId = LoginUtils.getLoginId();
        if(loginId!=null) {
            strictInsertFill(metaObject, "updateBy", String.class, loginId);
        }
    }
}
