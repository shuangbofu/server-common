package org.example.server.user.constant;

import org.example.server.web.ErrorCode;

public interface ErrorCodes {
    ErrorCode USER_NOT_FOUND = ErrorCode.define(10002, "用户不存在");
    ErrorCode USER_INVALID = ErrorCode.define(10003,"用户已失效");
    ErrorCode PASSWORD_WRONG = ErrorCode.define(10004, "用户%s密码错误");
    ErrorCode MOBILE_DUPLICATE = ErrorCode.define(10005, "手机号码%s重复");
    ErrorCode USERNAME_DUPLICATE = ErrorCode.define(10006, "用户名%s重复");
    ErrorCode ROLE_NAME_DUPLICATE = ErrorCode.define(20001, "角色名称「%s」重复");
}
