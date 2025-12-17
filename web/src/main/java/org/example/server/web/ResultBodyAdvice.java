package org.example.server.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.server.web.annotation.ResultBody;
import org.example.server.web.exception.BizException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class ResultBodyAdvice implements ResponseBodyAdvice {

    @PostConstruct
    public void init() {
        log.info("开启接口自动包装Result");
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        setupMapper(OBJECT_MAPPER);
    }

    public ResultBodyAdvice() {
    }

    public static ObjectMapper setupMapper(ObjectMapper mapper) {
        return mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return Objects.nonNull(returnType.getMethod()) &&
                (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResultBody.class) ||
                        returnType.hasMethodAnnotation(ResultBody.class));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  org.springframework.http.MediaType selectedContentType,
                                  Class selectedConverterType,
                                  org.springframework.http.server.ServerHttpRequest request,
                                  org.springframework.http.server.ServerHttpResponse response) {
        if (body == null) {
            return Result.ofSuccess("");
        }
        if (body instanceof String) {
            try {
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(Result.ofSuccess(body));
            } catch (JsonProcessingException e) {
                throw new BizException(ErrorCode.define("序列化错误"), e);
            }
        } else {
            return body instanceof Result<?> ? body : Result.ofSuccess(body);
        }
    }
}