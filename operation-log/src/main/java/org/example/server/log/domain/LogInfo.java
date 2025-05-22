package org.example.server.log.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class LogInfo implements Serializable {
    private String type;
    private String message;
    private Object oldValue;
    private Object newValue;

    public LogInfo(String type, String message, Object oldValue, Object newValue) {
        this.type = type;
        this.message = Optional.ofNullable(message).orElse("成功！");
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public LogInfo(String type, String message, Object newValue) {
        this(type, message, null, newValue);
    }

    public LogInfo(String type, Object oldValue, Object newValue) {
        this(type, null, oldValue, newValue);
    }

    public LogInfo(String type, Object newValue) {
        this(type, null, newValue);
    }
}
