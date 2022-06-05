package com.nowcoder.community;


import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class QuartzTests {
    @Autowired
    private Scheduler scheduler;

    //用来删除我们在测试的时候数据库的一些信息，当然了，不是所有都删除，有部分信息还是留在数据库中。
    @Test
    public void testDeleteJob() {
        try{
            boolean result = scheduler.deleteJob(new JobKey("alphaJob","alphaJobGroup"));
            System.out.println(result);
        } catch (SchedulerException e){
            e.printStackTrace();
        }

    }
}
