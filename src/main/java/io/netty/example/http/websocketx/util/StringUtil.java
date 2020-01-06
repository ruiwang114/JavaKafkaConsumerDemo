package io.netty.example.http.websocketx.util;

import com.alibaba.druid.util.StringUtils;

public class StringUtil {

    /**
     * 转为驼峰命名
     * @param str
     * @return string
     */
    public static String camelName(String str) {
        if (!StringUtils.isEmpty(str)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0, len = str.length(); i < len; i++) {
                if (str.charAt(i) == '_') {
                    while (str.charAt(i + 1) == '_') {
                        i++;
                    }
                    stringBuilder.append(("" + str.charAt(++i)).toUpperCase());
                } else {
                    stringBuilder.append(str.charAt(i));
                }
            }
            return stringBuilder.toString();
        }
        return str;
    }

    /**
     * 将第一个字母替换为大写
     * @param str
     * @return
     */
    public static String firstUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
    }
}
