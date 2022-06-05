package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private  String wkImageStorage;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    //输入网址格式http://127.0.0.1:8080/community/share?htmlUrl=https://www.baidu.com
    @RequestMapping(path = "/share", method = RequestMethod.GET)
    @ResponseBody//因为用了异步
    public String share(String htmlUrl) {
        //文件名
        String fileName = CommunityUtil.generateUUID();
        //异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);

        //返回访问路径
        Map<String, Object> map = new HashMap<>();
       //七牛云之前: map.put("shareUrl",domain + contextPath + "/share/image/" + fileName);//这个路径是配合下面展示图片用的
        map.put("shareUrl", shareBucketUrl + "/" + fileName);
        return  CommunityUtil.getJSONString(0,null,map);
    }

    //废弃，我们用七牛云啦
    //获取长图 生成长图完成会显示一个链接，输入链接即可展示图片
    @RequestMapping(path = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String filename, HttpServletResponse response) {
        if(StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        response.setContentType("image/png");
        try {
            File file=  new File(wkImageStorage + "/" + filename + ".png");
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != - 1) {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败" + e.getMessage());
        }


    }
}
