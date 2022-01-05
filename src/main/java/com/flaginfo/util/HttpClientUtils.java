package com.flaginfo.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author: yajun.xu@infocloud.cc
 * @data: 2021/08/24 14:32
 * @description: todo
 */
@Slf4j
public class HttpClientUtils {
    static final PoolingHttpClientConnectionManager cm;
    static RequestConfig requestConfig;

    static {
        LayeredConnectionSocketFactory factory = null;
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial((chain, authType) -> true).build();
            factory = new SSLConnectionSocketFactory(sslContext);

        } catch (Exception e) {
            log.error("连接池初始化失败", e);
        }
        assert factory != null;
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", factory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);

        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000).setConnectTimeout(30000).setSocketTimeout(30000).build();

    }

    private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(cm)
                .build();
    }


    public static String doPostWithHeader(String url, Map<String, Object> params, Map<String, Object> headers) {
        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json; charset=UTF-8");
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            for (Entry<String, Object> stringObjectEntry : headers.entrySet()) {
                httpPost.setHeader((String) ((Entry) stringObjectEntry).getKey(), (String) stringObjectEntry.getValue());
            }

            StringEntity strEntity = new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8);
            httpPost.setEntity(strEntity);
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception var10) {
            log.error("ERROR, call http post" + var10.getMessage(), var10);
            return null;
        }
    }


}