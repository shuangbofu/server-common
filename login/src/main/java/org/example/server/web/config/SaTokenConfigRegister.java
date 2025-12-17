package org.example.server.web.config;

import cn.dev33.satoken.config.SaTokenConfig;
import org.example.server.web.annotation.EnableLogin;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class SaTokenConfigRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(EnableLogin.class.getName()));

        if (mapperScanAttrs != null) {
            String tokenName = mapperScanAttrs.getString("tokenName");
            Number timeout = mapperScanAttrs.getNumber("timeout");
            Number activeTimeout = mapperScanAttrs.getNumber("activeTimeout");
            boolean isConcurrent = mapperScanAttrs.getBoolean("isConcurrent");
            boolean isShare = mapperScanAttrs.getBoolean("isShare");
            String tokenStyle = mapperScanAttrs.getString("tokenStyle");

            // Sa-Token 参数配置，参考文档：https://sa-token.cc
            BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(SaTokenConfig.class)
                    // token 名称（同时也是 cookie 名称）
                    .addPropertyValue("tokenName", tokenName)
                    // token 有效期（单位：秒），默认30天，-1代表永不过期
                    .addPropertyValue("timeout", timeout)
                    // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
                    .addPropertyValue("activeTimeout", activeTimeout)
                    // 是否允许同一账号多地同时登录（为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
                    .addPropertyValue("isConcurrent", isConcurrent)
                    // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
                    .addPropertyValue("isShare", isShare)
                    // token 风格
                    .addPropertyValue("tokenStyle", tokenStyle)
                    // 是否输出操作日志
                    .addPropertyValue("isLog", false)
                    .addPropertyValue("isPrint",false)
                    .setPrimary(true)
                    .getBeanDefinition();
            registry.registerBeanDefinition("saTokenConfig", definition);
        }
    }
}
