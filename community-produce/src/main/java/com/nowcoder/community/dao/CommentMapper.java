package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(@Param("entityType")int entityType, @Param("entityId") int entityId, @Param("offest") int offest, @Param("limit") int limit);//根据实体来查询，比如是查询帖子的评论，还是评论的评论

    int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);//查询数据条目数

    int insertComment(Comment comment);

    Comment selectCommentById(@Param("id") int id);//写系统通知的需要用

    List<Comment> selectCommentsByUser(@Param("userId")int userId, @Param("offset")int offset, @Param("limit")int limit);//我的评论

    int selectCountByUser(@Param("userId")int userId);
}
