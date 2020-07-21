package com.tanghs.elasticsearchlab.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.search.aggregations.Aggregations;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: tanghs
 * @date: 2019/12/5 19:49
 * @version:
 */
public interface SearchClient {
    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    boolean isIndexExists(String index);

    /**
     * 搜索结果
     *
     * @param request
     * @return
     */
    List<JSONObject> search(SearchRequest request);

    /**
     * 搜索结果
     *
     * @param request
     * @return
     */
    List<String> searchString(SearchRequest request);

    /**
     * 查询总数
     *
     * @param request
     * @return
     */
    Long searchCount(SearchRequest request);

    /**
     * 查询list数据
     *
     * @param request
     * @param tClass
     * @param <T>
     * @return
     */
    <T> List<T> search(SearchRequest request, Class<T> tClass);

    /**
     * 查询list数据 scroll 可拉取全量数据
     *
     * @param request
     * @param tClass
     * @param <T>
     * @return
     */
    <T> List<T> searchScroll(SearchRequest request, Class<T> tClass);

    /**
     * 插入数据，指定主键
     *
     * @param index 索引
     * @param type  类型
     * @param id    数据库主键ID
     * @param t     实体class
     * @param <T>
     * @return
     */
    <T> String saveEntity(String index, String type, String id, T t);

    /**
     * 插入数据，自动生成主键
     *
     * @param index 索引
     * @param type  类型
     * @param t     实体class
     * @param <T>
     * @return
     */
    <T> String saveEntity(String index, String type, T t);

    /**
     * 批量插入，自动生成主键
     *
     * @param index
     * @param type
     * @param t
     * @param <T>
     * @return
     */
    <T> String saveBulkEntity(String index, String type, List<T> t) throws IOException;

    /**
     * 指定主键，插入或更新
     *
     * @param index
     * @param type
     * @param id
     * @param t
     * @param <T>
     * @return
     */
    <T> UpdateResponse saveOrUpdateEntity(String index, String type, String id, T t);


    Aggregations getAggregations(SearchRequest request);
}
