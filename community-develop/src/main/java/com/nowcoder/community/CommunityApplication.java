//项目启动
package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {
    @PostConstruct
	public void init() {
    	//解决netty启动冲突问题
		//具体可以看netty4utils.java的setAvailableProcessors()
		System.getProperty("es.set.netty.runtime.available.processors","false");
	}
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
