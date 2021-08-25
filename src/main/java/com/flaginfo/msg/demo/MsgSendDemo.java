package com.flaginfo.msg.demo;

import com.alibaba.fastjson.JSON;
import com.flaginfo.util.HttpClientUtils;
import com.flaginfo.util.RandomUtils;
import com.flaginfo.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;

import java.util.*;

/**
 * @author: yajun.xu@infocloud.cc
 * @data: 2021/08/22 14:35
 * @description: 以下为 message/send 短信发送demo ,    message/sendBatch 类似,区别就是请求体参数不同
 */
@Slf4j
public class MsgSendDemo {


    public static void main(String[] args) throws Exception {



        // 从平台获取
        String apiKey = "你的apiKey";
        // 从平台获取
        String accesskey = "你的accesskey";
        // 多个手机号逗号分割
        String phones = "目标手机号1,目标手机号2";
        // 注意是审核通过的
        String templateCode = "你的模板编码";


        // 有参模板需要该参数,注意参数个数和模板参数个数对应, 模板的参数为字符串,数字也要转下string ; 无参模板不需要要传该参数
        // 错误示例(数字没转string):  String templateParam = JSON.toJSONString(Arrays.asList(1,2,3,5));
        // 正确写法:                 String templateParam = JSON.toJSONString(Arrays.asList("1","2","3","5"));
        String templateParam = JSON.toJSONString(Arrays.asList("模板第一个参数","模板的第二个参数","模板的第三个参数","第n个"));



        String nonce = RandomUtils.getRandomNickname(10);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signMethod = HmacAlgorithms.HMAC_SHA_224.getName(); // 加密算法 ,无特殊需求,无需改动


        // 1 .设置请求头
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("x-sign-method", signMethod);
        headers.put("x-nonce", nonce);
        headers.put("x-timestamp", timestamp);


        // 2. 设置参数
        Map<String, Object> params = new HashMap<>();
        params.put("phones", phones);
        params.put("templateCode", templateCode);
        // 模板有参数时,放开下面一行代码
        // params.put("templateParam",templateParam);


        // 3 .生成签名 ,并把签名加入请求头
        SortedMap<String, Object> sortedMap = new TreeMap<>(params);
        headers.forEach(sortedMap::put);
        headers.put("x-sign", SignUtils.getSignature(accesskey, sortedMap, HmacAlgorithms.HMAC_SHA_224)); // 加密算法 ,无特殊需求,无需改动

        // 4 . 发请求
        String res = HttpClientUtils.doPostWithHeader("https://opassapi.infocloud.cc/message/send", params, headers);
        log.info("响应报文:{}", JSON.toJSONString(res));
    }




}
