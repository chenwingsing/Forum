package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //返回的是一个集合，集合里面装着对象，后面传参是为了考虑未来我的帖子这个功能，所以有时候需要传参数(首页展示各种帖子不需要传id)，有时候不需要.offest起始行行号，limit一页显示多少。
    //原视频中是没有@Param三个的，但是这样会报错Parameter 'userId' not found.少写一个就多报一个错
    List<DiscussPost>  selectDiscussPosts(@Param("userId") int userId, @Param("offest")int offest, @Param("limit")int limit,@Param("orderMode") int orderMode);//orderMode默认是0，按原来排序，传入1就按热度排序


    //如果只有一个参数，并且在<if>(也就是动态SQL)里使用，就需要加上Param这个参数，这个参数还有个作用就是取别名。
    int selectDiscussPostRows(@Param("userId") int userId);
    int insertDiscussPost(DiscussPost discussPost);//发表帖子
    DiscussPost selectDiscussPostById(int id);//查询帖子id，用在帖子详情上

    int updateCommentCount(@Param("id")int id, @Param("commentCount") int commentCount);//更新评论数量

    int updateType(@Param("id") int id, @Param("type") int type); // '0-普通; 1-置顶;',

    int updateStatus(@Param("id")int id, @Param("status") int status); // '0-正常; 1-精华; 2-拉黑;',

    int updateScore(@Param("id")int id, @Param("score") double score); // 更新帖子分数




}
