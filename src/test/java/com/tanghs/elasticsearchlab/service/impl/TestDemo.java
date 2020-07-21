package com.tanghs.elasticsearchlab.service.impl;

import com.tanghs.elasticsearchlab.ElasticsearchLabApplication;
import com.tanghs.elasticsearchlab.model.Child;
import com.tanghs.elasticsearchlab.model.Parent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/5/29 09:00
 * @version:
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElasticsearchLabApplication.class})
public class TestDemo {
    @Test
    public void test() {
        Parent child = new Child();
    }
    @Test
    public void test1() {
        Long startTime = 1593741885130L;
        Long endTime = 1593741885138L;
        Long syncIndexTime = startTime != null ? startTime :(endTime != null ? endTime : System.currentTimeMillis());

        log.info("================ :{}",syncIndexTime);
    }

    @Test
    public void test2() {
        Long startTime = 1593741885138L;
        Long endTime = null;
        int value = startTime.compareTo(endTime);
        log.info("================ :{}",value);
    }

    @Test
    public void test3() {
        int page = 10035;
        int mod = 200;
        int v = page % mod;
        log.info("================ :{}",v);
    }
}
