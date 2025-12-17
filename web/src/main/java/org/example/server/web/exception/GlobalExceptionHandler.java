package org.example.server.web.exception;

import jakarta.annotation.PostConstruct;
import org.example.server.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @PostConstruct
    public void init() {
        log.info("开启全局异常校验");
    }
    /**
     * 其他错误
     *
     * @param e 异常
     * @return {@link Result}
     */
    @ExceptionHandler({Exception.class})
    public Result<String> exception(Throwable e) {
        log.error("未知错误: {}",e.getMessage(), e);
        return Result.ofFailed("-1","系统异常");
    }

    @ExceptionHandler({RuntimeException.class})
    public Result<String> exception(RuntimeException e) {
        log.error("其他错误: {}",e.getMessage(), e);
        return Result.ofFailed("-1",e.getMessage());
    }

    @ExceptionHandler({BizException.class})
    public Result<String> exception(BizException e) {
        log.warn("业务错误: {}",e.getMessage(), e);
        return Result.ofFailed(e.getCode()+"",e.getMessage());
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public Result<String> exception(NoResourceFoundException e) {
        return Result.ofFailed("404", e.getResourcePath()+" 404 NOT FOUND");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result<String> exception(MethodArgumentNotValidException e) {
        log.warn("校验错误: {}",e.getMessage() ,e);
        BindingResult bindingResult = e.getBindingResult();

        // 创建一个Map来存储字段错误信息
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage()); // 字段名和提示信息
        }
        return Result.ofFailed("-2",
                "校验失败:"+errors.keySet().stream()
                        .map(i-> i+errors.get(i)).collect(Collectors.joining(",")));
    }
}
