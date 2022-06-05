package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class CommunityApplicationTests implements ApplicationContextAware {

//	@Test
//	void contextLoads() {
//	}
    private ApplicationContext applicationContext;//用来记录这个容器
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void  testApplicationContext() {
		System.out.println(applicationContext);

		//获取这个bean并获得查询结果，输出为Hibernate
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		//通过名字来获取bean
		alphaDao = applicationContext.getBean("alphaHibernate",AlphaDao.class);//原视频中是name:"alphaHibernate",AlphaDao.class，新版idea已经不用了，可以自动识别
		System.out.println(alphaDao.select());
	}
	@Test
	public void testBeanManagement() {
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);//即便你写两次 也只是实例化一次，除非你注解scope,具体点开这个类的java文件看吧
	}
	@Test
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired//和上面的区别是，这样就不需要每次都getbean了
	@Qualifier("alphaHibernate")//因为我们之前默认Mybatis是默认，加上这个就会转到Hibernate，要注意后面不需要分号
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;
	@Test
	public void testDI() {//DI是依赖注入的意思
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);

	}

}
