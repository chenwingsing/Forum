package com.nowcoder.community.dao;


import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper //mybatis的注解，当然了你也可以写Repository（视频好像是这么说的，但是我试了会报错）
public interface UserMapper {
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);

    int insertUser(User user);//注意这个有点不一样，这个是一个对象，所以在mapper的xml在insert语句要声明
    int updateStatus(@Param("id") int id, @Param("status") int status);
    int updateHeader(@Param("id") int id, @Param("headerUrl") String headerUrl);
    int updatePassword(@Param("id") int id, @Param("password") String password);




}
