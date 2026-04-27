package org.example.vo_ai_service.xfyun.utils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpUtil {
    private HttpUtil() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final String UTF8 = "UTF-8";
    private static CloseableHttpClient httpClient;

    static {
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
        pool.setMaxTotal(600);
        pool.setDefaultMaxPerRoute(200);
        httpClient = HttpClients.createMinimal(pool);
    }

    public static String iflyrecUpload(String url, InputStream in) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(600000)
                .setSocketTimeout(600000).build();
        httpPost.setConfig(requestConfig);
        HttpEntity requestEntity = new InputStreamEntity(in, ContentType.APPLICATION_OCTET_STREAM);
        httpPost.setEntity(requestEntity);
        return doExecute(httpPost, null);
    }

    public static String iflyrecGet(String url) {
        HttpGet httpget = new HttpGet(url);
        return doExecute(httpget, UTF8);
    }

    public static String post(String url, byte[] body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new ByteArrayEntity(body, ContentType.create("application/octet-stream", Consts.UTF_8)));
        return doExecute(httpPost, UTF8);
    }

    public static String post(String url, String param) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000).build();
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new StringEntity(param, ContentType.APPLICATION_JSON));
        return doExecute(httpPost, null);
    }

    public static String sendGet(String url) {
        HttpGet httpget = new HttpGet(url);
        return doExecute(httpget, null);
    }

    private static String doExecute(HttpRequestBase requestBase, String charset) {
        String result = null;
        try (CloseableHttpResponse response = httpClient.execute(requestBase)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("网络异常, status:{}", statusCode);
                return null;
            }
            result = charset == null
                    ? EntityUtils.toString(response.getEntity())
                    : EntityUtils.toString(response.getEntity(), charset);
        } catch (Exception e) {
            LOGGER.error("网络异常", e);
        }
        return result;
    }

    public static String parseMapToPathParam(Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        try {
            Set<Entry<String, Object>> entryset = param.entrySet();
            boolean isFirst = true;
            for (Entry<String, Object> entry : entryset) {
                if (!isFirst) {
                    sb.append("&");
                } else {
                    isFirst = false;
                }
                sb.append(URLEncoder.encode(entry.getKey(), UTF8));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue().toString(), UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("HttpUtil parseMapToPathParam Exception!", e);
        }
        return sb.toString();
    }
}
