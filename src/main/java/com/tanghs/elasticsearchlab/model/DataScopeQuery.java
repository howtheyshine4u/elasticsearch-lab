package com.tanghs.elasticsearchlab.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: tanghs
 * @date: 2019/12/5 20:27
 * @version:
 */
@Data
public class DataScopeQuery implements Serializable {
    private Long creatorId;
    private List<Long> departmentIdList;
    private Long ownerId;
}
