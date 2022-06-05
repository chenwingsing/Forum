package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//用来描述方法，是一个自定义注解
@Retention(RetentionPolicy.RUNTIME)//声明有效时长，程序运行的时候有效
public @interface LoginRequired {//创建为注解的时候就自动有@
    //里面什么都可以不用写，只要打上这个标记，就说明必须登录的时候才能访问，比如静态资源setting，我们如果在没登录的时候也能访问就是bug
}
