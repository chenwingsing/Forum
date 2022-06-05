package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class ThreadPoolTests {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //JDK线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行任务线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private AlphaService alphaService;


    private void sleep(long m){//调用这个函数会阻塞线程
        try{
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //1.JDK普通线程池
    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {//线程体
            @Override
            public void run() {
                logger.debug("Hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);//sleep下，不然一下子就执行完了  10s
    }

    //JDK定时任务线程池
    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);//延迟10秒执行，不是立马执行，然后每隔1s执行一次，单位是毫秒
        sleep(30000);//30秒到了就结束
    }

    //spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    //spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 10000);//当前时间延迟10s
        threadPoolTaskScheduler.scheduleAtFixedRate(task, startTime, 1000);//默认毫秒单位，等10s后再执行
        sleep(30000);
    }

    //spirng普通线程池(简化版)
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }
        sleep(10000);//必须要阻塞，不然线程还没执行，方法就结果了
    }

    //spring定时任务线程池(简化版)
    @Test
    public void testThreadPoolTaskSchedulerSimple() {//只要有任务就会执行，这个任务我们在Alphaservice中写了，execute2()
        sleep(30000);
    }


}
