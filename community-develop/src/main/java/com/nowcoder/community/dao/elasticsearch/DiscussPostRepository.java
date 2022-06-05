package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository//注意不是mapper，那是mybatis专用的，repository是srping提供的
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
