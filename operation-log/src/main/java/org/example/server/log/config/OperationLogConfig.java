package org.example.server.log.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.server.log.aspect.OperationLogAspect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.URL;

@Configuration
@Slf4j
@Import({SpringContextHolder.class, OperationLogAspect.class})
@MapperScan(basePackages = "org.example.server.log.mapper.**")
@ComponentScan(basePackages = "org.example.server.log.controller.**")
public class OperationLogConfig {

    @PostConstruct
    public void init() {
        log.info("开启操作日志");
    }
}
