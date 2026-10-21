package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zeqi
 * @date 2026/10/20  21:06
 * @description 自定义注解，用于标识某个方法需要进行功能字段的自动填充处理
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //指定数据库操作类型
    OperationType value();
}
