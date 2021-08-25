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
     * @param accesskey 从平台获取额accesskey
     * @param sortedMap 请求体参数排序后的map
     * @param hmacAlgorithms 加密算法
     * @return
     */
    public static String getSignature(String accesskey, SortedMap<String, Object> sortedMap, HmacAlgorithms hmacAlgorithms) {
        //  将参数拼接为字符串
        //  e.g. "key1=value1&key2=value2"
        StringBuffer plainText = new StringBuffer();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            plainText.append(entry.getKey() + "=" + entry.getValue());
            plainText.append("&");
        }
        if(StringUtils.isNotEmpty(plainText)) {
            plainText.deleteCharAt(plainText.length() - 1);
        }
        return new HmacUtils(hmacAlgorithms, accesskey).hmacHex(plainText.toString());
    }
}