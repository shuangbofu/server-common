package org.example.server.web.exception;

import org.example.server.web.ErrorCode;

import java.io.Serial;

public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7787560787029293738L;
    private int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMsg(args));
        code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMsg(args), cause);
        code = errorCode.getCode();
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
    }

    public int getCode() {
        return code;
    }
}
