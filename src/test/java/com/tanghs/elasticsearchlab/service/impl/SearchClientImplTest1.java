package com.tanghs.elasticsearchlab.service.impl;

import cn.hutool.core.date.DateUtil;
import com.tanghs.elasticsearchlab.ElasticsearchLabApplication;
import com.tanghs.elasticsearchlab.model.*;
import com.tanghs.elasticsearchlab.service.SearchClient;
import com.tanghs.elasticsearchlab.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.ScriptQueryBuilder;

import org.elasticsearch.script.ScoreScript;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.rescore.QueryRescoreMode;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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
class SearchClientImplTest1 {
    private final String BREED_IDS = "breedIds";
    private final String BREED_IDS_COUNT = "breedIdsCount";
    @Autowired
    private SearchClient searchClient;

    @Test
    void testQuery() {
        SearchRequest request = new SearchRequest();
        request.indices("test_demo_2020");
        request.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[] keyWordArr = {"蔡徐坤", "生日"};
        String keyWord = "蔡徐坤 生日";
        //关键词搜索
        /*boolQueryBuilder.must(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery("title", "外国")));*/
        boolQueryBuilder.must(QueryBuilders.termQuery("name", "小蔡"));
        Map<String, Float> fields = new HashMap<>();
        fields.put("title", 1.2f);
        fields.put("contentPlainText", 1.0f);
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(keyWord).fields(fields).minimumShouldMatch("20%"));
        Optional.ofNullable(keyWordArr)
                .ifPresent(keywords -> {
                    BoolQueryBuilder keywordBuilder = QueryBuilders.boolQuery();
                    for (String keyword : keywords) {
                        keywordBuilder.should(QueryBuilders.boolQuery()
                                .must(QueryBuilders.matchPhraseQuery(TITLE, keyword)));
                        keywordBuilder.should(QueryBuilders.boolQuery()
                                .must(QueryBuilders.matchPhraseQuery("contentPlainText", keyword)));
                    }
                    boolQueryBuilder.must(keywordBuilder);
                });
        /*SearchResponse searchResponse = client.prepareSearch()
                .setIndices("test")
                .setTypes("test")
                .setQuery(QueryBuilders.matchQuery("name", "天津公安"))
                .addRescorer(new QueryRescorerBuilder(QueryBuilders.matchPhraseQuery("name", "天津公安")))
                .addRescorer(new QueryRescorerBuilder(
                        QueryBuilders.functionScoreQuery(
                                ScoreFunctionBuilders.scriptFunction("doc['time'].value / 10000")
                        )
                ).windowSize(100).setScoreMode(QueryRescoreMode.Multiply))
                .setFrom(0)
                .setSize(100)
                .execute()
                .actionGet();*/
        sourceBuilder.query(boolQueryBuilder);
        long startTime = CommonUtil.getTodayStartTime();
        long endTime = CommonUtil.getTodayEndTime();
        sourceBuilder.addRescorer(new QueryRescorerBuilder(QueryBuilders.rangeQuery("catchTime")
                .from(startTime).to(endTime)).windowSize(50).setRescoreQueryWeight(2.0f).setScoreMode(QueryRescoreMode.Total));

        log.info("sourceBuilder ====> {}", sourceBuilder.toString());
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        request.source(sourceBuilder);
        List<Person> list = searchClient.search(request, Person.class);
        log.info("list: {}", list.toString());
    }

    @Test
    void insert() {
        Person p = Person.builder()
                .id(11L)
                .name("小蔡")
                .title("蔡徐坤发布最新单曲")
                .contentPlainText("蔡徐坤鸡你太美，噢耶！")
                .catchTime(1594369170000L)
                .catchTimeDay("2020-07-10")
                .build();
        searchClient.saveEntity("test_demo_2020", "doc", p);
    }


    @Test
    void save() throws IOException {
        List<String> breedIds = new ArrayList<>();
        breedIds.add("10001");
        breedIds.add("10002");
        List<String> factoryIds = new ArrayList<>();
        factoryIds.add("10001");
        factoryIds.add("10002");
        List<String> cityIds = new ArrayList<>();
        cityIds.add("10001");
        List<String> portIds = new ArrayList<>();
        portIds.add("10001");

        UserSubscriptionTagsDTO userSubscriptionTags
                = UserSubscriptionTagsDTO.builder()
                .contentId(1L).contentName("肖氏称霸").breedIds(breedIds).breedIdsCount(breedIds.size())
                .factoryIds(factoryIds).factoryIdsCount(factoryIds.size())
                .cityIds(cityIds).cityIdsCount(cityIds.size()).portIds(portIds).portIdsCount(portIds.size()).channelId("4591").build();
        /*for (int i = 0; i < 50000 ; i++) {
            searchClient.saveEntity("qywx_subscription_tags_2020","doc",userSubscriptionTags);
        }*/
        long startTime = System.currentTimeMillis();
        /*List<UserSubscriptionTagsDTO> list = new ArrayList(){
            {
                for (int i = 0; i < 1 ; i++) {
                    add(userSubscriptionTags);
                }
            }
        };
        searchClient.saveBulkEntity("qywx_subscription_tags_2020","doc",list);*/
        searchClient.saveOrUpdateEntity("qywx_subscription_tags_2020", "doc", "10000", userSubscriptionTags);
        log.info("插入数据成功,duration:{} ms。", System.currentTimeMillis() - startTime);
    }

    @Test
    void testSearch() {
        SearchRequest request = new SearchRequest();
        request.indices("test_202005");
        request.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder breedBuilder = QueryBuilders.boolQuery();
        String[] breedIds = {"10001", "10002", "10003"};
        if (breedIds.length == 1) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(BREED_IDS, breedIds[0]))
                    .filter(QueryBuilders.termQuery(BREED_IDS_COUNT, 1));
        }
        if (breedIds.length == 2) {
            //1个值
            BoolQueryBuilder breedBuilder2 = QueryBuilders.boolQuery();
            BoolQueryBuilder breedBuilder2_1 = QueryBuilders.boolQuery();
            breedBuilder2_1.should(QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[0])));
            breedBuilder2_1.should(QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[1])));
            breedBuilder2.filter(breedBuilder2_1).filter(QueryBuilders.termQuery(BREED_IDS_COUNT, 1));
            //2个值
            BoolQueryBuilder breedBuilder4 = QueryBuilders.boolQuery();
            breedBuilder4.filter(QueryBuilders.termQuery(BREED_IDS, breedIds[0]))
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[1]))
                    .filter(QueryBuilders.termQuery(BREED_IDS_COUNT, 2));
            breedBuilder.should(breedBuilder2).should(breedBuilder4);
            boolQueryBuilder.filter(breedBuilder);
        }
        if (breedIds.length == 3) {
            //1个值
            BoolQueryBuilder breedBuilder3 = QueryBuilders.boolQuery();
            BoolQueryBuilder breedBuilder3_1 = QueryBuilders.boolQuery();
            breedBuilder3_1.should(QueryBuilders.termQuery(BREED_IDS, breedIds[0]))
                    .should(QueryBuilders.termQuery(BREED_IDS, breedIds[1]))
                    .should(QueryBuilders.termQuery(BREED_IDS, breedIds[2]));
            breedBuilder3.filter(breedBuilder3_1).filter(QueryBuilders.termQuery(BREED_IDS_COUNT, 1));
            //2个值
            BoolQueryBuilder breedBuilder_1 = QueryBuilders.boolQuery();
            BoolQueryBuilder breedBuilder_2 = QueryBuilders.boolQuery();
            BoolQueryBuilder breedBuilder_3 = QueryBuilders.boolQuery();
            BoolQueryBuilder breedBuilder4 = QueryBuilders.boolQuery();
            BoolQueryBuilder breedBuilder5 = QueryBuilders.boolQuery();
            breedBuilder_1.filter(QueryBuilders.termQuery(BREED_IDS, breedIds[0]))
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[1]));
            breedBuilder_2.filter(QueryBuilders.termQuery(BREED_IDS, breedIds[0]))
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[2]));
            breedBuilder_3.filter(QueryBuilders.termQuery(BREED_IDS, breedIds[1]))
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[2]));
            breedBuilder4.should(breedBuilder_1)
                    .should(breedBuilder_2)
                    .should(breedBuilder_3);
            breedBuilder5.filter(breedBuilder4)
                    .filter(QueryBuilders.termQuery(BREED_IDS_COUNT, 2));
            //3个值
            BoolQueryBuilder breedBuilder6 = QueryBuilders.boolQuery();
            breedBuilder6.filter(QueryBuilders.termQuery(BREED_IDS, breedIds[0]))
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[1]))
                    .filter(QueryBuilders.termQuery(BREED_IDS, breedIds[2]))
                    .filter(QueryBuilders.termQuery(BREED_IDS_COUNT, 3));
            breedBuilder.should(breedBuilder3)
                    .should(breedBuilder5)
                    .should(breedBuilder6);
            boolQueryBuilder.filter(breedBuilder);
        }
        sourceBuilder.query(boolQueryBuilder);
        log.info("sourceBuilder ====> {}", sourceBuilder.toString());
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        request.source(sourceBuilder);
        List<Breed> list = searchClient.search(request, Breed.class);
        log.info("list: {}", list.toString());
    }

    @Test
    void termsTest() {
        SearchRequest request = new SearchRequest();
        request.indices("test_202005");
        request.types("doc");
        String[] condition = {"爱情", "动作", "剧情", "悬疑", "战争"};
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("fileType", "爱情"));
        boolQueryBuilder.must(QueryBuilders.termQuery("fileType", "动作"));
        boolQueryBuilder.must(QueryBuilders.termQuery("fileType", "剧情"));
        boolQueryBuilder.must(QueryBuilders.termQuery("fileType", "悬疑"));
        Script script = new Script("doc['filmType'].length == 4");
        ScriptQueryBuilder scriptQueryBuilder = new ScriptQueryBuilder(script);
        boolQueryBuilder.must(scriptQueryBuilder);
        sourceBuilder.query(boolQueryBuilder);
        log.info("sourceBuilder ====> {}", sourceBuilder.toString());
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        request.source(sourceBuilder);
        List<Film> list = searchClient.search(request, Film.class);
        log.info("list: {}", list.toString());

    }


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

        Aggregations aggregation = searchClient.getAggregations(request);

        Terms aggTaskCycleId = aggregation.get("group_by_" + TASK_CYCLE_ID);


        for (Terms.Bucket entry : aggTaskCycleId.getBuckets()) {
            String key = (String) entry.getKey();            // bucket key
            long docCount = entry.getDocCount();            // Doc count
            log.info("key [{}], doc_count [{}]", key, docCount);

            // We ask for top_hits for each bucket
            TopHits topHits = entry.getAggregations().get("top");
            for (SearchHit hit : topHits.getHits().getHits()) {
                log.info(" -> id [{}], _source [{}]", hit.getId(), hit.getSourceAsString());
            }
        }
    }

    /**
     * sourceBuilder查询条件装载
     *
     * @param sourceBuilder
     */
    private void sourceBuilderLoading(SearchSourceBuilder sourceBuilder, CollectLogDTO dto) {
        AggregationBuilder taskIdAgg =
                AggregationBuilders
                        .terms("group_by_" + TASK_CYCLE_ID)
                        .field(TASK_CYCLE_ID)
                        .size(100)
                        .shardSize(10)  //分片返回数据数量，应大于等于返回数量
                        .order(BucketOrder.key(false))  //根据聚合字段值排序
                        //.order(BucketOrder.count(false)) //根据数量排序
                        .subAggregation(
                                AggregationBuilders
                                        .topHits("top")
                                        .explain(true)
                                        .size(1)
                                        .sort(COLLECT_STATUS, SortOrder.ASC)
                        );
        //不仅会影响它的hits的结果还会影响他的聚合（agg）结果
        sourceBuilder.query(QueryBuilders.termQuery(SPIDER_STAGE_TYPE, 1));
        //只影响hits的结果
        sourceBuilder.postFilter(QueryBuilders.termQuery(SPIDER_STAGE_TYPE, 1));
        sourceBuilder.aggregation(taskIdAgg);
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
}