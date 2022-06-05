package com.nowcoder.community.service;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById (int id) {
       // return userMapper.selectById(id);
        //重写了。用redis
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号是否被注册
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));//牛客网有随机1001个头像图片
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() +  "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;//如果有问题，map里面存的就是提示信息，如果没有问题，就正常注册，map就是空的
    }
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if(user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            //这里涉及更新，用redis清理掉缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password , long expiredSeconds) {//到期时间如果写Int，会出现点了记住我也是12小时，因为毫秒为单位会超过范围
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null) {
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0) {
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)) {
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));//毫秒为单位的 要*1000
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);//loginTicket是一个对象，然后用redis会序列化为一个json格式


        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    //退出登录
    public void logout(String ticket) {
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);//默认类型是object，要转成LoginTicket
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);//mysql中更新
        clearCache(userId);//然后清楚缓存，需要注意先更新再清理，因为更新有可能会出错，不能先清理缓存，但是老师这个写法，貌似没判断是否成功，我觉得应该再判断row是否为1才清理，1代表更新成功
        return rows;
    }

    //重置密码前校验邮箱
    public Map<String, Object> verifyEmail(String email) {
        Map<String,Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(email)) {
            //不需要写emailMsg，直接返回空的就行
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            //不需要写emailMsg，直接返回空的就行
            return map;
        } else {
            //如果能查到这个邮箱就发送邮件
            Context context = new Context();
            context.setVariable("email", email);
            String code = CommunityUtil.generateUUID().substring(0, 4);
            context.setVariable("verifyCode", code);
            String content = templateEngine.process("/mail/forget", context);
            mailClient.sendMail(email, "找回密码", content);
            map.put("code", code);
        }
        map.put("user", user);
        return map;
    }

    // 重置密码
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证邮箱
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未注册!");
            return map;
        }

        // 重置密码
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(), password);

        map.put("user", user);
        return map;
    }

    // 修改密码
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空!");
            return map;
        }



        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码输入有误!");
            return map;
        }

        // 更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);

        return map;
    }

    //根据用户名查询用户信息
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    //用Redis来缓存用户信息
    //1.优先从缓存中取值
    private User getCache(int userId) {//因为是userservice内部调用，所以用private
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //2.取不到初始化缓存
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);//需要用mysql
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);//一个小时有效期
        return user;
    }

    //3.当数据变更，清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
    //这个是增加了spring security后，根据用户来获取用户权限
    public Collection<? extends GrantedAuthority> getAuthorites(int userId) {
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
