package org.example.server.user.config;

import cn.dev33.satoken.secure.SaSecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.server.user.persistence.ObjectHandler;
import org.example.server.web.annotation.EnableLogin;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 全局加载/配置类
 */
@Configuration("userGlobalConfig")
// 开启登录功能
@EnableLogin
@ComponentScan(basePackages = {"org.example.server.user.**"})
@MapperScan(basePackages = {"org.example.server.user.persistence.mapper.**"})
@Slf4j
@Import(ObjectHandler.class)
public class GlobalConfig {
    @PostConstruct
    public void init() {
        log.info("开启用户&权限");
    }

    @Value("${user.default-pwd}")
    private String defaultPassword;

    @Getter
    @Value("${user.default-avatar-img-url}")
    private String defaultAvatarImgUrl;

    public String getDefaultPwd() {
        return SaSecureUtil.sha256(defaultPassword);
    }
}
