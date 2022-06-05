package com.nowcoder.community.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;

    @Qualifier("client")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //保存帖子
    public void saveDiscussPost(DiscussPost post) {
        discussRepository.save(post);
    }

    //删除帖子
    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }

    //搜索功能
    public List<DiscussPost> searchDiscussPost(String keyword) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()//type也就是是否置顶
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = elasticTemplate.search(searchQuery, DiscussPost.class);
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        //待返回集合
        List<DiscussPost> posts = new ArrayList<>();
        //将遍历结果进行处理
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            //高亮显示的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮内容填充到content中
            searchHit.getContent().setTitle(highlightFields.get("title") == null ? searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setContent(highlightFields.get("content") == null ? searchHit.getContent().getContent() : highlightFields.get("content").get(0));
            //传入返回集合
            posts.add(searchHit.getContent());
        }

        return posts;
    }

    //搜索的分页设置
    public <T> PageInfo<T> queryPageInfo(List<T> list, int currentPage, int pageSize) {
        int total = list.size();
        if (total > pageSize) {
            int toIndex = pageSize * currentPage;
            if (toIndex > total) {
                toIndex = total;
            }
            list = list.subList(pageSize * (currentPage - 1), toIndex);
        }
        Page<T> page = new Page<>(currentPage, pageSize);
        page.addAll(list);
        page.setPages((total + pageSize - 1) / pageSize);
        page.setTotal(total);
        PageInfo<T> pageInfo = new PageInfo<>(page);
        return pageInfo;
    }


}
