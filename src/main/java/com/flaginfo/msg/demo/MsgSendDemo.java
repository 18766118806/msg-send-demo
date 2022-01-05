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
 * @description: 以下为 message/send 短信发送demo ,    message/sendBatch 类似,区别就是接口url和请求体参数不同
 */
@Slf4j
public class MsgSendDemo {


    public static void main(String[] args) {


        // 从经分平台获取 ,登录经分助手>富媒体消息>接口信息>apiKey
        String apiKey = "你的apiKey";
        // 从经分平台获取, 登录经分助手>富媒体消息>接口信息>AccessKey
        String accessKey = "你的accessKey";
        // 多个手机号逗号分割
        String phones = "手机号1,手机号2";
        // 从经分平台获取 ,登录经分助手>富媒体消息>模板管理
        String templateCode = "审核通过的模板编号";


        // 无参模板不需要要templateParam参数, 有参模板需要该参数,注意参数个数和模板参数个数对应,顺序一致, 模板的参数为字符串,数字也要转下string ;
        // 例子,假如你的模板为: 您好:${姓名,10},今天是:${日期,20},温度:${温度,5}摄氏度.
        String templateParam = JSON.toJSONString(Arrays.asList("王校长", "2021年5月31日 星期三", "36.5"));

        String nonce = RandomUtils.getRandomNickname(10);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signMethod = HmacAlgorithms.HMAC_SHA_256.getName();  // 加密算法 ,无特殊需求,无需改动,注意和下面SignUtils.getSignature()使用算法相同


        // 1 .设置请求头参数 (请求头共五个参数)
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("x-sign-method", signMethod);
        headers.put("x-nonce", nonce);
        headers.put("x-timestamp", timestamp);


        // 2. 设置请求体参数 (无参模板
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("phones", phones);
        reqBody.put("templateCode", templateCode);
        // 无参模板不要要该参数,模板有参数时,放开下面一行代码
        //reqBody.put("templateParam", templateParam);


        // 3 .生成签名 ,并把签名加入请求头
        SortedMap<String, Object> sortedMap = new TreeMap<>(reqBody);
        headers.forEach(sortedMap::put);
        headers.put("x-sign", SignUtils.getSignature(accessKey, sortedMap, HmacAlgorithms.HMAC_SHA_256)); // HMAC_SHA_256 加密算法 ,无特殊需求,无需改动,需要和请求头 x-sign-method 保持一致

        // 4 . 发请求
        String res = HttpClientUtils.doPostWithHeader("https://opassapi.infocloud.cc/message/send", reqBody, headers);
        log.info("响应报文:{}", JSON.toJSONString(res));


        // 常见异常 :
        // 1 . 404 接口不存在 : 检查 HttpClientUtils.doPostWithHeader() 方法中的编码是否为 UTF-8
        // 2 . API授权失败或授权参数不完整
        //   a. 登录经分平台, 检查代码apiKey 和 accessKey 是否和平台一致 ;
        //   b. 检查http请求,请求头x-sign-method 值 和 生成签名x-sign 使用的算法是否一致 (签名算法没特殊需求,直接使用样例即可);
        //   c. 中文乱码,这个只能调用方解决, message/send 接口为例,templateParam 值尝试使用全英文或数字 ,如果能成功 ,但换成中文不行,就是乱码 .
        //       解决方案 : 首先检查http请求工具类请求头,请求体相关的字符集编码设置(UTF-8),建议直接使用样例代码,
        //                     本地可以发中文,部署到服务器不行,需要检查服务器配置,用到容器的话,容器要同步设置

        // 3 . 时间戳有误。请确认服务器是否存在时差或代码取值有误 .  校准服务器时间,北京时间30S内
    }


}
