package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    //事务测试
    @Test
    public void testSave1() {
        Object obj = alphaService.save1();
        System.out.println(obj);
    }

    //事务测试
    @Test
    public void testSave2() {
        Object obj = alphaService.save1();
        System.out.println(obj);
    }
}
