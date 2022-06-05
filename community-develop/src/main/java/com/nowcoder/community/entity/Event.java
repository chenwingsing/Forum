package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String topic;
    private int userId;//需要发给谁
    private int entityId;
    private int entityType;
    private int entityUserId;//帖子的作者是谁
    private Map<String, Object> data = new HashMap<>();//因为无法预判还需要什么数据，这里多一个map来存

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {//这个方法修改了返回类型，本来是void，这样写的好处是当我们调用set方法时候，肯定我们还要set其他属性，set完topic又返回当前对象，这样又可以调用其他对象方法
        this.userId = userId;
        return this;//额外增加的
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {//也是改了，原因一样
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {//也是改了，原因一样
        this.entityType = entityType;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

//    public void setData(Map<String, Object> data) {
//        this.data = data;
//    }
    //上面这个方法改造为
    public Event setData(String key, Object value) {//不想让外界直接传一个map，这样改调用更加方便
        this.data.put(key,value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityId=" + entityId +
                ", entityType=" + entityType +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }
}
