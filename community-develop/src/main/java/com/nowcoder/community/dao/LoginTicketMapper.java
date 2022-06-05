package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated//放弃这个代码了，因为我们用redis来操作啦，这样不用老是用mysql来提取
public interface LoginTicketMapper {
    //本例展示不写在dao中xml是如何更新数据库的，你当然也可以在dao中写这些
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",//注意语句后面要留个空格,然后系统会和下面的语句拼接，这样拼起来就有空格
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")//id设置自动生成
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({//这个只是举例如何用动态sql语句，实际上本业务只需要中间的update语句就行，动态sql要加script
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(@Param("ticket") String ticket,@Param("status") int status);

}
