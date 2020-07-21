package com.tanghs.elasticsearchlab.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * @description:
 * @author: tanghs
 * @date: 2019/10/28 17:25
 * @version:
 */
@Configuration
public class ElasticsearchConfig {
    @Value("${elasticsearch.clusterNodes}")
    private String clusterNodes;
    @Value("${elasticsearch.clusterName}")
    private String clusterName;

    public String getClusterNodes() {
        return clusterNodes;
    }

    public String getClusterName() {
        return clusterName;
    }

    /**
     * 初始化
     */
    @Bean(value = "highLevelClient")
    public RestHighLevelClient restHighLevelClient() {
        return getEsClientDecorator().getRestHighLevelClient();
    }

    @Bean
    @Scope("singleton")
    public ESClientDecorator getEsClientDecorator() {
        //可以配置集群 通过逗号隔开 10.204.58.170:9200,10.204.58.171:9200,10.204.58.172:9200
        String cNodes = this.clusterNodes;
        String[] node = cNodes.split(",");
        int length = node.length;
        HttpHost[] httpHost = new HttpHost[length];
        for (int i = 0; i < length; i++) {
            String[] nodes = node[i].split(":");
            httpHost[i] = new HttpHost(nodes[0], Integer.valueOf(nodes[1]));
        }
        return new ESClientDecorator(httpHost);
    }
}
