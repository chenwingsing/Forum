package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @PostConstruct
    public void init() {
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public List<DiscussPost> load(String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] params = key.split(":");
                        if (params ==  null || params.length != 2 ) {
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        //这里注释地方  可以加二级缓存 比如redis  -> mysql   (如果没有就去访问mysql)
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });

        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer key) throws Exception {
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //Caffeine核心接口:Cache, LoadingCache, AsyncLoadingCache
    //帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;
    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    //这里使用缓存
    //这里也是查询，可以直接调用mapper而不用新建这个service吗，答案是不行，即便是简单的查询也要写Service，因为后期可能加功能
    public List<DiscussPost> findDiscussPosts(int userId, int offest, int limit, int orderMode) {
        if(userId == 0 && orderMode ==1) {
            return postListCache.get(offest + ":" +limit);
        }
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offest, limit, orderMode);
    }

    //这里使用缓存
    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);//因为一定要求有一个key，随意即便传入的是0
        }
        logger.debug("load post rows from DB.");
        return  discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if(post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标签，因为怕有些故意在内容上写<script>之类的东西，会被浏览器识别为标签，加了这个可以让<script>被浏览器显示正常文本，而不是被识别为标签，具体可以看数据库看看存成什么格式
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getTitle()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
;