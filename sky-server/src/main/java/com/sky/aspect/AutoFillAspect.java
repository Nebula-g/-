package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author zeqi
 * @date 2026/10/20  21:12
 * @description 自定义切面类，用于实现功能字段的自动填充
 */
@Aspect// 切面注解
@Component// 组件注解，用于将切面类注册到Spring容器中，使其生效
@Slf4j// 日志注解
public class AutoFillAspect {
    // 切面类
    /**
     * 定义切入点
     */
    //先定好路径不用扫所有包，更效率（加上后只会扫描mp里带AutoFill注解的方法）
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")//路径.包.类.方法.参数
    public void autoFillPointCut() {}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("执行自动填充");
        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象(实体对象参数放在第一个位置，所以是0)
        Object[] args = joinPoint.getArgs();
        if(args==null||args.length==0) return;
        Object entity = args[0];
        log.info("实体对象：{}", entity);
        log.info("数据库操作类型：{}", operationType);
        

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，通过反射为对应的属性赋值
        if (operationType == OperationType.INSERT) {
            // 新增：创建时间/创建人 + 更新时间/更新人 4 个字段都要填
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("公共字段自动填充失败(INSERT)：{}", e.getMessage());
            }
        } else if (operationType == OperationType.UPDATE) {
            // 修改：只填更新时间/更新人，create 字段一律不碰（否则会把原始创建信息覆盖掉）
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("公共字段自动填充失败(UPDATE)：{}", e.getMessage());
            }
        }

    }

}
