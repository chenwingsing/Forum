package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")//访问数据库就要用这个，后面加个名字就是能够在强制访问中使用

public class AlphaDaoHibernateImpl implements  AlphaDao{

    @Override
    public String select() {
        return "Hibernate";
    }
}
