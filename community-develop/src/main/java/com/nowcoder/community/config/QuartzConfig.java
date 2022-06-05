package com.nowcoder.community.config;


import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import com.nowcoder.community.quartz.WKImageDeleteJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置 -> 数据库 ->调用  quartz只在第一次的时候需要配置，后面就直接调用数据库
@Configuration
public class QuartzConfig {

    //FactoryBean可简化Bean的实例化过程:
    //1.通过FactoryBean封装Bean的实例化过程
    //2.将FactoryBean装配到spring容器中
    //3.将FactoryBean注入到其他Bean
    //4.将Bean得到的是FactoryBean所管理的对象实例


    //配置JobDetail
    //@Bean 我们第一次学习的时候要注入，现在可以取消了，具体看7.13的视频最后面 不然不取消每次启动都会运行
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);//持久保存
        factoryBean.setRequestsRecovery(true);//有问题的话可以恢复
        return factoryBean;
    }

    //配置Trigger(SimpleTriggerFactoryBean, CropTriggerFactoryBean)
    //@Bean 我们第一次学习的时候要注入，现在可以取消了，具体看7.13的视频最后面 不然不取消每次启动都会运行
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {//alphaJobDetail就是上面那个方法
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);//3000ms执行一次
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    //刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);//持久保存
        factoryBean.setRequestsRecovery(true);//有问题的话可以恢复
        return factoryBean;
    }
    //刷新帖子分数触发器
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);//5分钟一次
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    // 删除WK图片任务
    @Bean
    public JobDetailFactoryBean wkImageDeleteJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(WKImageDeleteJob.class);
        factoryBean.setName("wkImageDeleteJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 删除WK图片触发器
    @Bean
    public SimpleTriggerFactoryBean wkImageDeleteTrigger(JobDetail wkImageDeleteJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(wkImageDeleteJobDetail);
        factoryBean.setName("wkImageDeleteTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 4);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
