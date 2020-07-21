package com.tanghs.elasticsearchlab.model;

import lombok.*;

import java.util.List;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/5/15 14:50
 * @version:
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionTagsDTO {
    /**
     * 内容id   es入库时作为id
     */
    private Long contentId;
    /**
     * 内容名称
     */
    private String contentName;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 品种id数组
     */
    private List<String> breedIds;
    /**
     * 品种个数
     */
    private Integer breedIdsCount;

    /**
     * 工厂id数组
     */
    private List<String> factoryIds;
    /**
     * 工厂个数
     */
    private Integer factoryIdsCount;
    /**
     * 城市id数组
     */
    private List<String> cityIds;
    /**
     * 城市个数
     */
    private Integer cityIdsCount;
    /**
     * 港口id数组
     */
    private List<String> portIds;
    /**
     * 港口个数
     */
    private Integer portIdsCount;
    /**
     * 频道
     */
    private String channelId;
}
