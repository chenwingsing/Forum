package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component

public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        try{
            MimeMessage message = mailSender.createMimeMessage();//模板
            MimeMessageHelper helper = new MimeMessageHelper(message);//用于构建内容
            helper.setFrom(from);//设置发件人
            helper.setTo(to);//设置收件人
            helper.setSubject(subject);
            helper.setText(content,true);//设置内容,后面true代表发生的是html，不加则是普通文本
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            //e.printStackTrace();
            logger.error("发生邮件失败" + e.getMessage());
        }

    }
}
