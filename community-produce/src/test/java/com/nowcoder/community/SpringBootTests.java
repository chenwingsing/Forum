package com.nowcoder.community;


import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class SpringBootTests {

    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    @BeforeClass//因为这个注解所修饰的方法是个类初始化之前，只执行一次，和类有关的，所以是静态的static
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {//也是只调用一次的
        System.out.println("afterClass");
    }

    @Before   //每次调用方法都执行，所以没有static
    public void before() {
        System.out.println("before");

        //初始化测试数据
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    @After
    public void after() {
        System.out.println("after");
        //删除测试数据
        //discussPostService.updateStatus(data.getId(),2);
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());//判断是否相等
        Assert.assertEquals(data.getContent(),post.getContent());
    }

    @Test
    public void testUpdateScore() {
        int row = discussPostService.updateScore(data.getId(), 2000.00);
        Assert.assertEquals(1,row);
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertEquals(2000.00,post.getScore(),2);//判断到两位小数，因为计算机浮点数不是很准确
    }

}
