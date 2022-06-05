package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration//表示这个类是配置类
public class AlphaConfig {
    @Bean //加载一个第三方的bean,也就是下面这个方法返回的对象装配到容器里，bean的名字是simpleDateFormat
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");

    }
}
