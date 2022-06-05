package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name) {
        if(request == null || name == null) {
            throw new IllegalArgumentException("参数为空");
        }
        Cookie[] cookies = request.getCookies();//老师没说为什么是数组,可以打印试试。我知道了，因为cookie有里面有很多名字，比如JSESSIONID，还有你自己设置的ticket
        if(cookies != null) {
            for(Cookie cookie:cookies) {
                if (cookie.getName().equals(name)) {//一直找到ticket这个名字先
                    return cookie.getValue();//然后返回
                }
            }
        }
        return null;
    }
}
