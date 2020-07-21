package com.tanghs.elasticsearchlab.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tanghs.elasticsearchlab.service.SearchClient;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: tanghs
 * @date: 2019/12/5 19:50
 * @version:
 */
@Slf4j
@Service("searchClient")
public class SearchClientImpl implements SearchClient {
    @Qualifier("highLevelClient")
    @Autowired
    private RestHighLevelClient client;


    @Override
    public boolean isIndexExists(String index) {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        request.local(false);
        request.humanReadable(true);
        try {
            boolean exist = client.indices().exists(request, RequestOptions.DEFAULT);
            return exist;
        } catch (IOException e) {
            log.error("判断索引是否存在时IOException：{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("判断索引是否存在时Exception：{}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<JSONObject> search(SearchRequest request) {
        List<JSONObject> list = null;
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (response.getHits() == null) {
                return null;
            }
            list = new ArrayList<>();
            List<JSONObject> finalList = list;
            response.getHits().forEach(item -> finalList.add(JSON.parseObject(item.getSourceAsString())));
            log.info("Hits:{}", response.getHits().toString());
        } catch (Exception e) {
            log.error("search exception:{}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<String> searchString(SearchRequest request) {
        List<String> list = null;
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            if (response.getHits() == null) {
                return null;
            }
            list = new ArrayList<>();
            List<String> finalList = list;
            response.getHits().forEach(item -> finalList.add(item.getSourceAsString()));
        } catch (Exception e) {
            log.error("searchString exception:{}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Long searchCount(SearchRequest request) {
        long total = 0L;
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            if (response.getHits() == null) {
                return 0L;
            }

            total = response.getHits().totalHits;

        } catch (Exception e) {
            log.error("searchCount exception:{}", e.getMessage(), e);
        }
        return total;
    }

    @Override
    public <T> List<T> search(SearchRequest request, Class<T> tClass) {

        List<JSONObject> searchResponse = this.search(request);
        if (searchResponse == null) {
            return null;
        }
        List<T> list = new ArrayList<>(searchResponse.size());
        searchResponse.forEach(item -> list.add(JSON.parseObject(JSON.toJSONString(item), tClass)));
        return list;
    }

    @Override
    public <T> String saveEntity(String index, String type, String id, T t) {
        IndexResponse indexResponse = null;
        try {
            IndexRequest indexRequest = new IndexRequest(index, type, id);
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            indexResponse = this.client.index(indexRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            log.error("saveEntity exception:{}", e.getMessage(), e);
        }
        return String.valueOf(indexResponse);
    }

    @Override
    public <T> String saveEntity(String index, String type, T t) {
        IndexResponse indexResponse = null;
        try {
            IndexRequest indexRequest = new IndexRequest(index, type);
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            indexResponse = this.client.index(indexRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            log.error("saveEntity exception: {}", e.getMessage(), e);
        }
        return String.valueOf(indexResponse);
    }

    @Override
    public <T> String saveBulkEntity(String index, String type, List<T> list) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        list.forEach(t -> {
            IndexRequest indexRequest = new IndexRequest(index, type);
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulkResponse = this.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return String.valueOf(bulkResponse);
    }

    @Override
    public <T> List<T> searchScroll(SearchRequest searchRequest, Class<T> tClass) {
        List<T> list = null;
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] hits = searchResponse.getHits().getHits();
            log.debug("first scroll:");
            list = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                list.add(JSON.parseObject(searchHit.getSourceAsString(), tClass));
                log.debug("第一页：{}", searchHit.getSourceAsString());
            }

            Scroll scroll = new Scroll(TimeValue.timeValueMinutes(5L));
            log.debug("loop scroll:");
            while (hits != null && hits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                hits = searchResponse.getHits().getHits();
                for (SearchHit searchHit : hits) {
                    list.add(JSON.parseObject(searchHit.getSourceAsString(), tClass));
                    log.debug("下一页：{}", searchHit.getSourceAsString());
                }
            }
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
            log.debug("clearScroll: {}!", succeeded);
        } catch (Exception e) {
            log.error("searchScroll exception:{}", e.getMessage(), e);
        }
        return list;
    }

    /**
     * 更新操作，原记录不存在，使用saveOrUpdate模式。
     */
    @Override
    public <T> UpdateResponse saveOrUpdateEntity(String index, String type, String id, T t) {
        UpdateResponse result = null;
        try {
            UpdateRequest updateRequest = new UpdateRequest(index, index, id);
            IndexRequest indexRequest = new IndexRequest(index, type, id);
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            updateRequest.doc(indexRequest);
            updateRequest.upsert(indexRequest);
            updateRequest.doc(indexRequest);
            //upsert 方法表示如果数据不存在，那么就新增一条，默认是false。
            updateRequest.docAsUpsert(true);
            result = client.update(updateRequest, RequestOptions.DEFAULT);
            log.debug("updateResponse : {}", updateRequest);
        } catch (Throwable e) {
            log.error("saveOrUpdateEntity exception: {}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Aggregations getAggregations(SearchRequest request) {
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("exception::{}", e.getMessage(), e);
        }
        return response.getAggregations();
    }
}
