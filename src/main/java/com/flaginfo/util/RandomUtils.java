package com.flaginfo.util;

import java.util.Random;

/**
 * @author: yajun.xu@infocloud.cc
 * @data: 2021/08/25 16:34
 * @description: todo
 */
public class RandomUtils {

    /**
     * nonce 生成
     * @param length
     * @return
     */
    public static String getRandomNickname(int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (choice + random.nextInt(26)));
            } else { // 数字
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }
}
