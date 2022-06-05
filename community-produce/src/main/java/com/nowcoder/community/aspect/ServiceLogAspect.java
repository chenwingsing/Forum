package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
//统一日志管理
@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..) )")//service下所有方法（*.）的所有参数(*(..))
    public void pointcut() {
    //不用写任何逻辑,仅仅是为了定义一个切点而已,逻辑都在那个注解上
    }

    @Before("pointcut()")
    public  void before(JoinPoint joinPoint) {
        //用户[1.2.3.4]在xxx访问了com.nowcoder.community.service.xxx()
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {//一开始是没有这句，但是发现系统推送通知的时候出错，
            return;//之前我们没有生产者与消费者，我们所有对service的访问都是通过controller访问的，但是现在消费者调用了service，而不是通过controller去调用的，所以request是空的，attributes就得不到
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." +joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
       //用户[127.0.0.1],在[2022-05-19 21:04:35],访问了[com.nowcoder.community.service.MessageService.findLetterUnreadCount].
    }
}

