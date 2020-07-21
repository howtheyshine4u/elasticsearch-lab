package com.tanghs.elasticsearchlab.model;

import lombok.*;

import java.io.Serializable;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/7/9 13:53
 * @version:
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = 213728370688115428L;
    /**
     * id
     */
    private Long id;
    /**
     * name
     */
    private String name;

    private String title;

    private String contentPlainText;
    /**
     * catchTime
     */
    private Long catchTime;
    /**
     * day
     */
    private String catchTimeDay;
}
