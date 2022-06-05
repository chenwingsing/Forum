package com.nowcoder.community.controller;

import com.github.pagehelper.PageInfo;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        //搜索帖子
        List<DiscussPost> searchResult =  elasticsearchService.searchDiscussPost(keyword);
        PageInfo<DiscussPost> queryPageInfo = elasticsearchService.queryPageInfo(searchResult, page.getCurrent(),page.getLimit());
        List<DiscussPost> pageInfoList = queryPageInfo.getList();
        //聚合数据
        List<Map<String, Object>> discussPosts =  new ArrayList<>();
        if(!pageInfoList.isEmpty()) {
            for(DiscussPost post : pageInfoList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);//帖子
                map.put("user",userService.findUserById(post.getUserId()));//作者
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));//点赞数量
                discussPosts.add(map);
            }
        }
        System.out.println(discussPosts);
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);//顺便把keyword传进来
        //分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : searchResult.size());//searchResult.size()
        return "/site/search";
    }


}
