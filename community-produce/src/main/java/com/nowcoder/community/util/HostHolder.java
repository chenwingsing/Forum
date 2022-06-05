package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

@Component//放在容器里要加Component
//持有用户信息，用于代替session对象
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();//方法都是从这个对象引入的

    public void setUsers(User user) {//具体看源码这个方法是通过线程来获取的，因为服务器可能同时收到多个请求,这部分涉及多线程
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
