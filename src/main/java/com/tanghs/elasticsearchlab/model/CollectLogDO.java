package com.tanghs.elasticsearchlab.model;

import lombok.*;

/**
 * @description:
 * @author: tanghs
 * @date: 2019/12/5 20:29
 * @version:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CollectLogDO {

    /**
     * 采集任务ID
     */
    private Long id;

    /**
     * 采集任务周期执行任务唯一标识
     */
    private String taskCycleId;

    /**
     * 列表/文章
     */
    private Integer spiderStageType;
    /**
     * 列表/文章详情
     */
    private String spiderStageTypeDes;

    /**
     * 采集模块
     */
    private Integer collectModule;
    /**
     * 采集模块描述
     */
    private String collectModuleDes;

    /**
     * 0：测试任务，1：正式任务
     */
    private Integer env;
    /**
     * 环境名称
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
     * 耗时
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

}
