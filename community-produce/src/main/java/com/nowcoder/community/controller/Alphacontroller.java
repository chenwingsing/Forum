package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class Alphacontroller {
    @Autowired
    private AlphaService alphaService;//Controller依赖于Servive

    @RequestMapping("hello")
    @ResponseBody
    public String sayHello(){
        return "Hello spring boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();//输入http://127.0.0.1:8080/alpha/data后：Controller会调用Service的find方法，然后find方法调用Dao的select方法，我们Dao设置了Mybatis为默认
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();//迭代器对象
        while(enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));//视频是name："code"，但是已经idea改了， http://127.0.0.1:8080/alpha/http?code=123
        //返回相应数据
        response.setContentType("text/html;charset=utf-8");//可以显示中文
        try{
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //GET请求
    // /students?current=1&limit=20   当前第几页每页几条数据
    @RequestMapping(path = "/students", method = RequestMethod.GET)//强制只能请求get，不然可以post或者get
    @ResponseBody
    public String getStudents(
            //下面这样设置是为了第一次打开可能没有值返回，就先设置一个默认的
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123  指定学生
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // POST请求 通常提交数据用POST，get请求通常数据量有限
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody   //不加这个无法显示succes，我猜是为了响应网页
    // http://127.0.0.1:8080/html/student.html
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";//会跳到/alpha/student
    }

    //响应Html数据
    //http://127.0.0.1:8080/alpha/teacher
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");//实质是view.html 在templates中
        return mav;
    }

    //另外一种方式，和上面的做对比，下面这个方式更加简单，开发尽量用这个
    //http://127.0.0.1:8080/alpha/teacher
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/view";
    }

    //响应JSON数据(异步请求)
    //Java对象 -> JSON字符串 -> JS对象
    //http://127.0.0.1:8080/alpha/emp  浏览器显示{"name":"张三","salary":8000.0,"age":23}
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody//不加会认为你返回 html
    public Map<String, Object> getemp() {
        Map<String,Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody//不加会认为你返回 html
    //http://127.0.0.1:8080/alpha/emps  浏览器显示[{"name":"张三","salary":8000.0,"age":23},{"name":"张","salary":7000.0,"age":24},{"name":"是","salary":99000.0,"age":27}]
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp =new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp =new HashMap<>();
        emp.put("name", "张");
        emp.put("age", 24);
        emp.put("salary", 7000.00);
        list.add(emp);

        emp =new HashMap<>();
        emp.put("name", "是");
        emp.put("age", 27);
        emp.put("salary", 99000.00);
        list.add(emp);
        return list;
    }
    //cookie example
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    //要把cookie存到response里 所以才有这个参数 HttpServletResponse response
    public String setCookies(HttpServletResponse response) {
        //创建cookie
        Cookie cookie = new Cookie("code",CommunityUtil.generateUUID());
        //设置生效范围，也就是访问其他路径是无效的
        cookie.setPath("/community/alpha");
        //cookie生存时间 秒为单位 下面是10分钟
        cookie.setMaxAge(60 * 10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    //session example
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //AJAX example
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

}
