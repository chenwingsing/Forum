package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offest, int limit) {
        return messageMapper.selectConversations(userId, offest, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offest, int limit) {
        return messageMapper.selectLetters(conversationId, offest, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    //发送私信
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    //设置已读
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids,1);//因为消息是很多条的，这样可以一起改
    }

    //删除私信
    public int deleteMessage(int id) {
        return messageMapper.updateStatus(Arrays.asList(new Integer[]{id}), 2);
        //Arrays.asList数组转化成List集合  如[358]这种形式
    }

    //查看最新消息
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    //查询通知数量
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }
    //查询未读通知数量
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }
    //查询通知
    public List<Message> findNotices(int userId, String topic,int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
