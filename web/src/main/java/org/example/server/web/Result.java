package org.example.server.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private boolean success;

    private String code;

    private String message;

    private T data;

    public static <T> Result<T> ofSuccess(T data) {
        return new Result<>(true, "200","", data);
    }

    public static <T> Result<T> ofFailed(String code, String message) {
        return new Result<>(false, code, message, null);
    }
}

