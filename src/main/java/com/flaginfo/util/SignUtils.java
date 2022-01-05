package com.flaginfo.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.SortedMap;

/**
 * @author: yajun.xu@infocloud.cc
 * @data: 2021/08/24 14:34
 * @description: todo
 */
@Slf4j
public class SignUtils {

    /**
     * 签名生成
     * @param accessKey 从平台获取accessKey
     * @param sortedMap 请求体参数排序后的map
     * @param hmacAlgorithms 加密算法
     * @return
     */
    public static String getSignature(String accessKey, SortedMap<String, Object> sortedMap, HmacAlgorithms hmacAlgorithms) {
        //  将参数拼接为字符串
        //  e.g. "key1=value1&key2=value2"
        StringBuffer plainText = new StringBuffer();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            plainText.append(entry.getKey()).append("=").append(entry.getValue());
            plainText.append("&");
        }
        if(StringUtils.isNotEmpty(plainText)) {
            plainText.deleteCharAt(plainText.length() - 1);
        }
        //无参 plainText 顺序 : phones=手机号1,手机号2&templateCode=审核通过的模板编号&x-api-key=你的apiKey&x-nonce=72qB2Fxn23&x-sign-method=HmacSHA256&x-timestamp=1641350089343
        //有参 plainText 顺序 : phones=手机号1,手机号2&templateCode=审核通过的模板编号&templateParam=["王校长","2021年5月31日 星期三","36.5"]&x-api-key=你的apiKey&x-nonce=H95a7XT1h6&x-sign-method=HmacSHA256&x-timestamp=1641350184346
        return new HmacUtils(hmacAlgorithms, accessKey).hmacHex(plainText.toString());
    }
}