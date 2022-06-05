package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";//分隔符
    private static final String PREFIX_ENTITY_LIKE = "like:entity";//前缀形式 记录不同实体，比如帖子，回复，评论
    private static final String PREFIX_USER_LIKE = "like:user";//用于方便统计个人收到多少赞;
    private static final String PREFIX_FOLLOWEE = "followee";//被关注者，粉丝（存的值是我的偶像）
    private static final String PREFIX_FOLLOWER = "follower";//关注者，追随者(存的值是我的粉丝)
    private static final String PREFIX_KAPTCHA = "kaptcha";//验证码
    private static final String PREFIX_TICKET = "ticket";//登录凭证
    private static final String PREFIX_USER = "user";//登录凭证
    private static final String PREFIX_UV = "uv";//独立访客
    private static final String PREFIX_DAU = "dau";//日活跃用户
    private static final String PREFIX_POST = "post";//计算帖子热度排行用到的，先把这些帖子存起来



    //某个实体的赞
    //格式为like:entity:entityType:entityId -> set(userId) 便于知道是谁点赞的
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT +entityType +SPLIT +entityId;
    }

    //某个用户的赞
    //like:user:userId -> int
    public static String getUserLikeKey (int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体 可以关注帖子，人
    //followee:userId:entityType -> zset(entityID,now) 用有序集合，用当前时间排序
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId -> zset(entityID,now) 用有序集合，用当前时间排序
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType +SPLIT +entityId;
    }
    //登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT  + ticket;
    }

    //用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT  + userId;
    }

    //当日UV
    public static String getUVKey(String date) {
        return PREFIX_UV  + SPLIT + date;
    }

    //区间UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT +date;
    }

    //区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate +SPLIT + endDate;
    }

    //帖子分数 这个是存多个帖子，所以不需要传帖子ID
    public static String getPostScoreKey() {
        return PREFIX_POST  + SPLIT  + "score";
    }
}
