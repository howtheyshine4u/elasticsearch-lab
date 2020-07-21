package com.tanghs.elasticsearchlab.model;

import lombok.*;

/**
 * @description:
 * @author: tanghs
 * @date: 2019/12/5 20:11
 * @version:
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectLogDTO {

    //采集任务ID
    private Long id;

    // 采集任务周期执行任务唯一标识
    private String taskCycleId;

    /**
     * 列表/文章
     */
    private Integer spiderStageType;
    private String spiderStageTypeDes;


    /**
     * CollectModuleEnum 采集模块
     */
    private Integer collectModule;
    private String collectModuleDes;

    /**
     * 0：测试任务，1：正式任务
     */
    private Integer env;
    /**
     *
     */
    private String envName;

    /**
     * 分类ID
     */
    private Long catalogId;
    /**
     * 分类名称
     */
    private String catalogName;

    /**
     * 新闻源ID
     */
    private Long newsfeedId;
    /**
     * 新闻源名称
     */
    private String newsfeedName;

    /**
     * 任务名称
     */
    private String name;
    /**
     * 采集状态
     */
    private Integer collectStatus;

    /**
     * 采集状态描述
     */
    private String collectStatusDes;
    /**
     * 耗时 秒
     */
    private Long timeConsuming;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章URL
     */
    private String targetUrl;
    /**
     * 采集地址（列表页）
     */
    private String url;
    /**
     * 备注
     */
    private String description;
    /**
     * 日志创建时间
     */
    private Long createTime;

    /**
     * 任务开始时间点
     */
    private Long taskStartTime;

    /**
     * 前端传过来的范围  yyyy-MM-dd  -  yyyy-MM-dd
     */
    private String toAndFromTime;

    /**
     * 最后采集时间段，查询范围结束时间
     */
    private Long toTime;
    /**
     * 最后采集时间段，查询范围开始时间
     */
    private Long fromTime;

    /**
     * 文章详情ID
     */
    private Long spiderArticleId;

    /**
     * 部门Id
     */
    private Long departmentId;
    /**
     * 数据权限查询
     */
    private DataScopeQuery dataScopeQuery;
    /**
     * 所属人ID
     */
    private Long ownerId;

    /**
     * 查询数量
     */
    private Long count;

    /**
     * 该任务日志是否全部查询（列表。详情）
     */
    private Boolean isFindAll;
}
