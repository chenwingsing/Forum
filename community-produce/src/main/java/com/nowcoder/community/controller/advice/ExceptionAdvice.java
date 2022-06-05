package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


//处理异常 这样写不需要在任何一个Controller中处理，这里可以全部处理
@ControllerAdvice(annotations = Controller.class)//只去扫描Controller的bean
public class ExceptionAdvice {

        private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

        @ExceptionHandler({Exception.class})
        public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws Exception {//方法必须是public
            logger.error("服务器发生异常"+e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }
            //区分是返回异步请求还是普通请求
            String xRequestedWith = request.getHeader("x-requested-with");
            if("XMLHttpRequest".equals(xRequestedWith)) {//异步请求的话
                response.setContentType("application/plain;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
            } else {//普通请求
                response.sendRedirect(request.getContextPath() + "/error");

            }

        }
}
