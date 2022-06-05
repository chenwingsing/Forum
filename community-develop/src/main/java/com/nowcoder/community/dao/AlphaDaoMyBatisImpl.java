package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary //可以看到我们有两个类来实现AlphaDao，而我们希望优先选择这个，加了primary就会优先选，也就是变成默认
public class AlphaDaoMyBatisImpl implements  AlphaDao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
