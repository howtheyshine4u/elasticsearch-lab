package com.tanghs.elasticsearchlab.service.impl;

import cn.hutool.core.date.DateUtil;
import com.tanghs.elasticsearchlab.ElasticsearchLabApplication;
import com.tanghs.elasticsearchlab.model.CollectLogDO;
import com.tanghs.elasticsearchlab.model.CollectLogDTO;
import com.tanghs.elasticsearchlab.service.SearchClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.tanghs.elasticsearchlab.common.Constants.*;


/**
 * @description:
 * @author: tanghs
 * @date: 2019/12/5 19:57
 * @version:
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElasticsearchLabApplication.class})
// 指定启动类
class SearchClientImplTest {
    @Autowired
    private SearchClient searchClient;

    @Test
    void search() throws Exception {
        CollectLogDTO dto = CollectLogDTO.builder().build();
        //es查询
        SearchRequest request = new SearchRequest();
        //获取索引
        String[] indexs = getIndexs(dto);
        //索引名称
        request.indices(indexs);
        //类型
        request.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //多查询条件装载
        sourceBuilderLoading(sourceBuilder, dto);
        log.info("sourceBuilder ====> {}", sourceBuilder.toString());
        request.source(sourceBuilder);

        //查询总数count
        long count = searchClient.searchCount(request);
        log.info("count = " + count);
        //当前面数
        int pageNum = Math.min(1, 100);
        //一页查询个数
        int pageSize = 100;

        //设置分页
        int from = (pageNum - 1) * pageSize;
        //from size分页（数据量10万级以下使用）
        List<CollectLogDO> list = searchPageFromSize(request, sourceBuilder, from, pageSize);

        log.info("查询列表：list = " + list.toString());

    }

    /**
     * from size 分页
     *
     * @param request
     * @param sourceBuilder
     * @param from
     * @param pageSize
     * @return
     */
    private List<CollectLogDO> searchPageFromSize(SearchRequest request, SearchSourceBuilder sourceBuilder,
                                                  int from, int pageSize) {
        sourceBuilder.from(from);
        sourceBuilder.size(pageSize);
        request.source(sourceBuilder);
        //查询分页列表
        return searchClient.search(request, CollectLogDO.class);
    }

    /**
     * sourceBuilder查询条件装载
     *
     * @param sourceBuilder
     */
    private void sourceBuilderLoading(SearchSourceBuilder sourceBuilder, CollectLogDTO dto) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //查询详情
        if (dto.getTaskCycleId() != null) {
            if (dto.getIsFindAll() != null) {
                if (dto.getIsFindAll()) {
                    QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                            .filter(QueryBuilders.termQuery(TASK_CYCLE_ID, dto.getTaskCycleId()))
                            .filter(QueryBuilders.termQuery(SPIDER_STAGE_TYPE, 2)); //DETAIL
                    boolQueryBuilder.should(queryBuilder);
                    QueryBuilder findAll = QueryBuilders.boolQuery()
                            .filter(QueryBuilders.termQuery(TASK_CYCLE_ID, dto.getTaskCycleId()))
                            .filter(QueryBuilders.termQuery(SPIDER_STAGE_TYPE, 1)) //LIST
                            .filter(QueryBuilders.termQuery(COLLECT_STATUS, 2));
                    boolQueryBuilder.should(findAll);
                }
            } else {
                boolQueryBuilder.filter(QueryBuilders.termQuery(TASK_CYCLE_ID, dto.getTaskCycleId()));
                boolQueryBuilder.filter(QueryBuilders.termQuery(SPIDER_STAGE_TYPE, 2));
            }
            sourceBuilder.query(boolQueryBuilder);
            //设置排序
            sourceBuilder.sort(CREATE_TIME, SortOrder.DESC);
            return;
        }
        //查询所有 此处为匹配所有文档
        boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        //环境
        Optional.ofNullable(dto.getEnv())
                .ifPresent(env -> boolQueryBuilder.must(QueryBuilders.termQuery(ENV, env)));
        //任务开始时间
        Optional.ofNullable(dto.getFromTime())
                .ifPresent(fromTime -> boolQueryBuilder.must(QueryBuilders.rangeQuery(TASK_START_TIME)
                        .from(fromTime).to(dto.getToTime())));
        //列表
        boolQueryBuilder.must(QueryBuilders.termQuery(SPIDER_STAGE_TYPE, 1));
        //模块名称
        Optional.ofNullable(dto.getCollectModule())
                .ifPresent(model -> boolQueryBuilder.must(QueryBuilders.termQuery(COLLECT_MODULE, model)));
        //分类ID
        Optional.ofNullable(dto.getCatalogId())
                .ifPresent(catalogId -> boolQueryBuilder.must(QueryBuilders.termQuery(CATALOG_ID, catalogId)));
        //新闻源ID NEWSFEED_ID
        Optional.ofNullable(dto.getUrl())
                .ifPresent(url -> boolQueryBuilder.must(QueryBuilders.termQuery(URL, url)));
        //任务名称 NAME
        Optional.ofNullable(dto.getName())
                .ifPresent(name -> boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(NAME, name)));
        //采集状态 COLLECT_STATUS
        Optional.ofNullable(dto.getCollectStatus())
                .ifPresent(status -> boolQueryBuilder.must(QueryBuilders.termQuery(COLLECT_STATUS, status)));
        //文章标题 TITLE
        Optional.ofNullable(dto.getTitle())
                .ifPresent(title -> boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(TITLE, title)));
        //备注 全文匹配
        Optional.ofNullable(dto.getDescription())
                .ifPresent(des -> boolQueryBuilder.must(QueryBuilders.matchQuery(DESCRIPTION, des)));
        //权限
        Optional.ofNullable(dto.getDataScopeQuery())
                .ifPresent(dataQuery -> {
                    //creatorId,ownerId
                    Long creatorId = dataQuery.getCreatorId();
                    Long ownerId = dataQuery.getOwnerId();
                    if (creatorId != null && ownerId != null) {
                        QueryBuilder rightControl = QueryBuilders.boolQuery()
                                .should(QueryBuilders.boolQuery()
                                        .filter(QueryBuilders.termQuery(CREATOR_ID, creatorId)))
                                .should(QueryBuilders.boolQuery()
                                        .filter(QueryBuilders.termQuery(OWNER_ID, ownerId)));
                        boolQueryBuilder.must(rightControl);
                    }
                    if (creatorId != null && ownerId == null) {
                        boolQueryBuilder.must(QueryBuilders.termQuery(CREATOR_ID, creatorId));
                    }
                    if (creatorId == null && ownerId != null) {
                        boolQueryBuilder.must(QueryBuilders.termQuery(OWNER_ID, ownerId));
                    }
                    //departmentId
                    List<Long> departmentList = dataQuery.getDepartmentIdList();
                    Optional.ofNullable(departmentList)
                            .ifPresent(list -> {
                                if (list.size() > 0) {
                                    boolQueryBuilder.must(QueryBuilders.termsQuery(DEPARTMENT_ID, list));
                                }
                            });
                });
        sourceBuilder.query(boolQueryBuilder);
        //设置排序
        sourceBuilder.sort(TASK_START_TIME, SortOrder.DESC);
    }

    /**
     * 根据查询条件时间范围，获取索引
     *
     * @param dto
     * @return
     */
    private String[] getIndexs(CollectLogDTO dto) throws Exception {
        Long fromTime = Optional.ofNullable(dto.getFromTime()).orElse(System.currentTimeMillis());
        Date fromDate = new Date(fromTime);
        Long toTime = Optional.ofNullable(dto.getToTime()).orElse(System.currentTimeMillis());
        Date toDate = new Date(toTime);
        //计算月份差
        @SuppressWarnings("unchecked")
        int monthCount = (int) DateUtil.betweenMonth(fromDate, toDate, true);
        List<String> indexList = new ArrayList<>();
        //添加起始日期索引
        addIndex(indexList, fromDate);
        //添加月份差索引
        if (monthCount > 0) {
            for (int i = 1; i <= monthCount; i++) {
                addIndex(indexList, DateUtils.addMonths(fromDate, i));
            }
        }
        if (indexList.isEmpty()) {
            log.error("查询的索引均不存在，fromDate: {} ，toDate: {} ", fromDate, toDate);
            throw new Exception("查询的索引均不存在！");
        }
        return indexList.toArray(new String[indexList.size()]);
    }

    /**
     * 添加索引进索引数组
     *
     * @param indexList
     * @param date
     */
    public void addIndex(List<String> indexList, Date date) {
        String index = "newspider_collectlog_" + DateUtil.format(date, YEAR_MONTH);
        Boolean indexExist = searchClient.isIndexExists(index);
        if (indexExist) {
            indexList.add(index);
        }
    }
    @Test
    public void test() {
        int pageNo = (1000 + 200 - 1) / 200;
        System.out.println("========:pageNo = " + pageNo);
    }
}