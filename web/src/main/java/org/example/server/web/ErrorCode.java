package org.example.server.web;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ErrorCode implements Serializable {
    @Serial
    private static final long serialVersionUID = 8500539248596237505L;
    private String msg;
    private Integer code;

    public ErrorCode(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public static ErrorCode define(String msg) {
        return new ErrorCode(-1, msg);
    }

    public static ErrorCode define(int code, String msg) {
        return new ErrorCode(code, msg);
    }

    public String getMsg(Object... args) {
        return msg.formatted(args);
    }
}
