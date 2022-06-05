package com.nowcoder.community.controller;


import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //统计页面
    @RequestMapping(path = "/data", method = {RequestMethod.GET,RequestMethod.POST})//既可以post请求，也可以get请求
    public String getDataPage() {
        return "/site/admin/data";
    }

    //统计网站UV
    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){//默认传回来是日期的字符串，所以要用DateTimeFormat转一下
        long uv = dataService.calculateUV(start,end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        return "forward:/data";//这个意思就是本次请求处理一半，然后交给/data，也就是上面的统计方法复用，这也解释了为什么统计方法上有post请求，因为这个是post请求，转发实际上是同一个请求，所以你转到/data也是post，另外实际上这里也可以直接写return "/site/admin/data";
    }

    //活跃用户
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){//默认传回来是日期的字符串，所以要用DateTimeFormat转一下
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        return "forward:/data";//这个意思就是本次请求处理一半，然后交给/data，也就是上面的统计方法复用，这也解释了为什么统计方法上有post请求，因为这个是post请求，转发实际上是同一个请求，所以你转到/data也是post，另外实际上这里也可以直接写return "/site/admin/data";
    }



}
