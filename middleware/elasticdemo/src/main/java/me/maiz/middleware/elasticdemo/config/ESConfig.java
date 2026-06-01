package me.maiz.middleware.elasticdemo.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static co.elastic.clients.elasticsearch.connector.ConnectorFieldType.List;

@Configuration
@Slf4j
public class ESConfig {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String esUri;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 解析 URI
        String host = "localhost";
        int port = 9200;
        String scheme = "http";

        if (esUri != null && !esUri.isEmpty()) {
            // 处理 http://localhost:9200 格式
            String uri = esUri.replace("http://", "").replace("https://", "");
            if (esUri.startsWith("https://")) {
                scheme = "https";
            }
            String[] parts = uri.split(":");
            host = parts[0];
            if (parts.length > 1) {
                port = Integer.parseInt(parts[1]);
            }
        }

        // 创建凭证提供者
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (username != null && !username.isEmpty()) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            log.info("ES认证已配置 - 用户: {}", username);
        } else {
            log.warn("ES未配置认证信息");
        }

        // 构建 RestClient
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, scheme))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        RestClient restClient = builder.build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        log.info("Elasticsearch客户端已初始化 - {}: {}:{}", scheme, host, port);
        return new ElasticsearchClient(transport);
    }

}
