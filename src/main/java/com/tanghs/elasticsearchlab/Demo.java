package com.tanghs.elasticsearchlab;

import com.mysteel.util.URLEncoder;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/5/9 19:21
 * @version:
 */
public class Demo {
    public static void main(String[] args) {
        String name = "2020年520会员日活动";
        name = URLEncoder.encode(name,"UTF-8");
        System.out.println(name);
    }
}
