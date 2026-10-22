package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zeqi
 * @date 2026/10/21  23:31
 * @description 阿里云OSS配置类,用于创建OssUtil工具类对象
 */
@Configuration//标识这是一个配置类
@Slf4j
public class OssConfiguration {
    @Bean//标识这是一个Bean对象，可以被Spring容器管理
    @ConditionalOnMissingBean//标识当Spring容器中没有该类型的Bean对象时，才创建该Bean对象
    public AliOssUtil ossUtil(AliOssProperties aliOssProperties) {
        log.info("创建OssUtil工具类对象");
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
