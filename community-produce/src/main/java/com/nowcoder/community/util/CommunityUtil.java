package com.nowcoder.community.util;





import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//uuid可以保证不重复https://zhuanlan.zhihu.com/p/70375430
public class CommunityUtil {
    //生成随机字符串 用自带的UUID
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密，但是这个加密每次都是固定的，有规律的，所以我们还要用盐加密
    public static String md5(String key) {
        if(StringUtils.isBlank(key)) {
            return null;//判断空
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//spring自带工具，把结果加密成16进制然后返回，但是你的参数得转成byte
    }
//ajax相关工具，下面三个都是
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }

}
