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
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpClientUtils {
    static final PoolingHttpClientConnectionManager cm;
    static RequestConfig requestConfig;

    private HttpClientUtils() {
    }

    private static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom().disableConnectionState().disableAutomaticRetries()
                .setConnectionManager(cm).build();
        return httpClient;
    }


    public static String doPostWithHeader(String url, Map<String, Object> params, Map<String, Object> headers) {
        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json; charset=UTF-8");
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            Iterator var5 = headers.entrySet().iterator();

            while (var5.hasNext()) {
                Entry<String, String> entry = (Entry) var5.next();
                httpPost.setHeader(entry.getKey(), entry.getValue());
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
//            log.error("ERROR, call http post" + var10.getMessage(), var10);
            var10.printStackTrace();
            return null;
        }
    }

    static {
        LayeredConnectionSocketFactory factory = null;
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();

            factory = new SSLConnectionSocketFactory(sslContext,
//					new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
                    new String[]{"TLSv1.2"}, null,
                    NoopHostnameVerifier.INSTANCE);
//			SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        } catch (Exception e) {
//            log.error("连接池初始化失败",e);
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", factory).register("http", new PlainConnectionSocketFactory()).build();
        //设置持久链接的存活时间TTL（timeToLive），其定义了持久连接的最大使用时间，超过其TTL值的链接不会再被复用
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry, null, null, null, 60, TimeUnit.SECONDS);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        cm.setValidateAfterInactivity(1000);
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000).setConnectTimeout(30000)
                .setSocketTimeout(30000).build();
    }
}