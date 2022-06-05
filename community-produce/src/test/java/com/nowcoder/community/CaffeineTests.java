package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class CaffeineTests {
    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 100; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网小陈专招计划");
            post.setContent("小陈要拿到秋招offer啊");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            postService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,0));
    }
}
