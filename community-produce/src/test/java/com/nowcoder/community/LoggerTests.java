package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.net.ServerSocket;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class LoggerTests {
    //为了便于所有方法调用，用static
    private static final Logger  logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {
        System.out.println(logger.getName());
        //trace是最低级别，一般不用，还要别忘了在application.properties配合
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }
}
