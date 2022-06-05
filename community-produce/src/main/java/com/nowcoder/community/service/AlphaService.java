package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")//有了这个，你每次getbean都会实例化一次，但是一般来说项目都是用单例，这个不多用
public class AlphaService {
    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired
    private AlphaDao alphaDao;//Service依赖于dao查询，本项目是把dao当成数据库查询

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
        System.out.println("实例化AlphaService");

    }
    @PostConstruct//加上这个注解意思是 这个方法会在构造器之后调用
    public void init() {
        System.out.println("初始化AlphaService");
    }
    @PreDestroy//销毁对象前调用，因为你要是销毁了就没法调用了
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }



    //事务演示 声明式事务，一般多用这个
    //A事务调B事务，对于B来说，A就是当前事务（也就是外部事务）
    //Propagation.NESTED 如果当前存在事务(即外部事务)，则嵌套在该事务中执行（独立的提交和回滚），否则就会REQUIRED一样。
    //Propagation.NEW 创建新事务，并且暂停当前事务（外部事务）
    //Propagation.REQUIRED:支持当前事务(外部事务)，如果不存在则创建新事务
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");//故意写错的，看看是不是会回滚，也就是不执行上面两个操作
        return "ok";
    }

    //事务example
    //编程式事务 如果业务比较复杂 可以用这个
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");//故意写错的，看看是不是会回滚，也就是不执行上面两个操作
                return "ok";
            }
        });
    }

    //让该方法在多线程环境下，被异步调用
    @Async
    public void execute1() {
        logger.debug("execute1");
    }

    //@Scheduled(initialDelay = 10000, fixedDelay = 1000)//10s后执行，执行的时候每次隔1s  取消掉 不然一直运行 测试的时候用而已
    public void execute2() {
        logger.debug("execute2");
    }

}
