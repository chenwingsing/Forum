package com.nowcoder.community.actuator;


import com.nowcoder.community.util.CommunityUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//http://127.0.0.1:8080/community/actuator/database
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    @ReadOperation
    public String checkConnection() {
        try (Connection conn = dataSource.getConnection();){//  在小括号内写可以自动关闭连接
            return CommunityUtil.getJSONString(0,"获取连接成功");

        } catch (SQLException e) {
            logger.error("获取连接失败");
            return CommunityUtil.getJSONString(1,"获取连接失败");
        }

    }

}
