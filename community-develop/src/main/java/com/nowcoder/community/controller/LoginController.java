package com.nowcoder.community.controller;


import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kapthaProducer;//kaptcha依赖安装好后可以出现producer

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextpath;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String getForgetPage() {
        return "/site/forget";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)//这里配置的是资源的访问路径，不是实际本机存放的路径，一开始以为/site/register
    public String register(Model model, User user) {
        //user.getUsername()和user.getEmail())可以获取到前台输入的信息，然后再给register去判断
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()) { //注册成功情况
            model.addAttribute("msg","注册成功，已经将激活邮件发送至邮箱");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        } else {
            System.out.println(map.get("usernameMsg"));
            System.out.println(map.get("passwordMsg"));
            System.out.println(map.get("emailMsg"));
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    //http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        //model用于传参，@PathVariable从路径中取值
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功，您的账号已经正常使用");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT) {
            model.addAttribute("msg","无效激活，您的账号已经激活过");
            model.addAttribute("target","/index");
        } else {
            model.addAttribute("msg","激活失败，您的激活码不对");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";
    }
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        //生成验证码
        String text = kapthaProducer.createText();
        BufferedImage image = kapthaProducer.createImage(text);
        //将验证码存入session
        //session.setAttribute("kaptcha",text);

        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);//60秒过期
        cookie.setPath(contextpath);
        response.addCookie(cookie);

        //将验证码存入redis
        String rediskey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(rediskey, text, 60, TimeUnit.SECONDS);//超过60s失效
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)//上面也有一个路径是用login，但是get，如果相同的会就冲突。
    public String login(String username, String password, String code, boolean rememberme, Model model, /*HttpSession session,*/ HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner) {
        //String kaptcha = (String)session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)) {//判断是否失效
            String rediskey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(rediskey);//得到的是object，转string
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            //验证码有问题，传入验证码是空的，然后验证码不区分大小写比较不相等
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";//如果没有这句话，会同时保存账号和密码的msg，但是这样不好，我们首先就要判断验证码是否正确
        }

        //检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        if(map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());//我们得到的是一个对象，要转字符串
            cookie.setPath(contextpath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);//响应的时候会发送给浏览器
            return "redirect:/index";//不写redirect会出错
        } else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();//退出登录也要清理哦
        return "redirect:/login";//重定向默认是get请求，别忘了之前我们写过两个login，一个post，一个get
    }

    // 获取验证码并验证邮箱是否存在
    @RequestMapping(path = "/forget/code", method = RequestMethod.GET)
    @ResponseBody
    public String getForgetCode(String email, HttpSession session, Model model) {
        if (StringUtils.isBlank(email)) {
            return CommunityUtil.getJSONString(1, "邮箱不能为空！");
        }
        Map<String, Object> map = userService.verifyEmail(email);
        if(map.containsKey("user")) {//判断是否有邮箱的注册信息

            // 保存验证码
            session.setAttribute("verifyCode", map.get("code"));
            return CommunityUtil.getJSONString(0);
        } else {
            return CommunityUtil.getJSONString(1, "查询不到该邮箱注册信息");
        }
    }

    // 重置密码
    @RequestMapping(path = "/forget/password", method = RequestMethod.POST)
    public String resetPassword(String email, String verifyCode, String password, Model model, HttpSession session) {
        String code = (String) session.getAttribute("verifyCode");
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(verifyCode)) {
            model.addAttribute("codeMsg", "验证码错误!");
            return "/site/forget";
        }

        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            model.addAttribute("msg","修改密码成功，请重新登录");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }

    // 修改密码
    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/logout";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }


}
