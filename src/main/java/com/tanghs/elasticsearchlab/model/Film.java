package com.tanghs.elasticsearchlab.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/5/16 17:17
 * @version:
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    /**
     * 电影类型
     */
    private String[] filmType;
    /**
     * 电影类型数组个数
     */
    private Integer fileTypeCount;

}
