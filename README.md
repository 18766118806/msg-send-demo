# msg-send-demo

注意以下几点 :

message/send 接口 与 message/sendBatch 接口 1,2,3 通用 ,区别在4,5


 
【 1 】接口为 post 请求
 
【 2 】请求头设置:
                Accept:application/json;charset=utf-8
                Content-Type:application/json;charset=utf-8
      请求体设置:
                Content-Type:application/json;charset=utf-8
 

【 3 】 请求头参数:
                1. x-api-key          字符串     必传    登录平台获取
                2. x-sign-method      字符串     必传    加密算法,枚举类HmacAlgorithms
                3. x-nonce            字符串     必传    通过 getRandomNickname(10)获取
                4. x-timestamp        字符串     必传    当前时间戳 ,(注意校准服务器时间)
                5. x-sign             字符串     必传    通过签名生成工具类获取 SignUtils.getSignature
 
【 4 】 message/send 接口,请求体参数 :
                1. phones         必传        字符串           多个手机号逗号分隔 eg: "1876611,1876688,1876600"
                2. templateCode   必传        字符串           登录平台获取,必须是审核通过的模板编号
                3. templateParam  非必传      集合json字符串    JSON.toJSONString(Array.asList("参数1","参数2"...,"参数n")),注意:参数1,参数2,参数n,都为字符串格式!!!!!, 若模板为无参模板,可以不传 ,
 
【 5 】 message/sendBatch 接口,请求体参数 :
                1. phonesJson         必传        json字符串         JSON.toJSONString(Array.asList("手机号1","手机号2"))  ,eg : ["手机号1","手机号2"]
                2. templateCode       必传        字符串           登录平台获取,必须是审核通过的模板编号
                3. templateParamJson  必传       集合json字符串    JSON.toJSONString(Array.asList(Array.asList("第一个手机号模板参数1","第一个手机号模板参数2"),Array.asList("第二个手机号模板参数1","第二个手机号模板参数2"))),
                                                                     注意:模板所有参数,都为字符串!!!!, eg : [["第一个手机号模板参数1","第一个手机号模板参数2"],["第二个手机号模板参数1","第二个手机号模板参数2"]]
 
注 : 有的公司可能会禁用fastjson (漏洞多) ,不管用什么序列化工具,序列化后的json字符串与示例结果格式相同即可



重点 : templateParam, phonesJson, templateParamJson 参数值为json字符串,里面元素为String类型!!!

  错误示例 :
        templateParam : [1,2,3]
        phonesJson: [1876611,1876622,1876633]
        templateParamJson: [[1,2],[3,5]]

  正确示例 :
         templateParam : ["1","2","3"]
         phonesJson: ["1876611","1876622","1876633"]
         templateParamJson: [["1","2"],["3","5"]]

