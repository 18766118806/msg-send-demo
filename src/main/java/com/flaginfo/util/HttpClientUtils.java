package com.flaginfo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

//@Slf4j
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
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

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


    /**
     * java 原生http请求
     * @param url
     * @param body   请求体
     * @param header 请求头
     * @return
     */
    public static JSONObject doPostOfJava(String url, Map<String, Object> body, Map<String, Object> header) {
        try {

            String postBody = JSON.toJSONString(body);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            // 请求头默认的参数,你也可以加一些其他的
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // 请求头加入自定义的参数
            header.keySet().forEach(s -> conn.setRequestProperty(s, String.valueOf(header.get(s))));

            // 连接设置
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(postBody.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().flush();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                //
                return null;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return JSON.parseObject(result.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            //  logger.error("invoke throw exception, details: " + e);
        }
        return null;
    }


}