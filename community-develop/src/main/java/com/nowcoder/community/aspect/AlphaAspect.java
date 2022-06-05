package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
//测试用而已 暂时注释
//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..) )")//service下所有方法（*.）的所有参数(*(..))
    public void pointcut() {

    }

    @Before("pointcut()")
    public  void before() {
        System.out.println("before");
    }

    @After("pointcut()")
    public  void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public  void afterReturning() {
        System.out.println("afterreturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }
}
