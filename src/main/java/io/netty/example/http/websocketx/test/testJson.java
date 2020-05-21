package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Set;

public class testJson {

    public static void main(String[] args) {
        String str="[{\n" +
                "              \"userUuid\": \"130086FFCCB34D7AAD35FF3A9C58876F\",\n" +
                "              \"ifWhite\": 0,\n" +
                "              \"status\": 0,\n" +
                "              \"company\": \"重庆盛佳元电子商务有限公司\",\n" +
                "              \"province\": \"重庆市\",\n" +
                "              \"city\": \"市辖区\",\n" +
                "              \"area\": \"渝中区\",\n" +
                "              \"ifOwner\": 1,\n" +
                "              \"ifUserEvent\": 0\n" +
                "            }]";
        JSONArray array=JSONArray.parseArray(str);
        System.out.println(array);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aaa","111");
        jsonObject.put("ccc","333");
        jsonObject.put("ddd","444");
        jsonObject.put("bbb","222");
        test(jsonObject);
        System.out.println(jsonObject);
    }
    private static String test(JSONObject jsonObject){
        Set<String> keySet= jsonObject.keySet();
        keySet.remove("bbb");
        String[] ss = new String[keySet.size()];
        keySet.toArray(ss);
        Arrays.sort(ss);
        for (int i = 0; i < ss.length; i++) {
            System.out.println(ss[i]);
        }
        return "";
    }
}
