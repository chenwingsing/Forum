package com.nowcoder.community;


import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.ContextConfiguration;


import java.util.LinkedList;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;

    @Qualifier("client")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test//一条条数据插入
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {//根据用户id批量来存，因为一个用户可能发了很多帖子，所以我们设置每个用户100条
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134,0,100,0));
    }

    @Test//更新数据
    public void tsetUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        discussRepository.save(post);
    }

    @Test
    public void testDelete() {
        discussRepository.deleteById(231);//删除231这条
        //discussRepository.deleteAll();//所有数据删除
    }

    @Test//高亮查询
    public void testSearchByRepository() throws Exception{
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)// 指定从哪条开始查询
                .size(10)// 需要查出的总记录条数
                .highlighter(highlightBuilder);//高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<DiscussPost> list = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);

            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            System.out.println(discussPost);
            list.add(discussPost);
        }
    }

}
