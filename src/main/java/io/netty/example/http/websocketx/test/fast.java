package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import io.netty.example.http.websocketx.entity.ThreatInfo;


public class fast {

    private static String jsonStr="";

    public static ThreatInfo serialize() {
        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.setOperation("=");
        threatInfo.setInfoId("18");
        threatInfo.setIndustryCode("GA");
        threatInfo.setStartTime("2019-09-10 02:58:55");
        threatInfo.setEndTime("2019-12-16 15:35:22");
        threatInfo.setTotal("6680");
        threatInfo.setScore("80");
        threatInfo.setDstCompany("一所");
        threatInfo.setSrcCountry("中国");
        threatInfo.setSrcProvince("天津");
        threatInfo.setSrcCity("天津");
        jsonStr = JSON.toJSONString(threatInfo);
        System.out.println(jsonStr);
        return threatInfo;
    }

    public static void deserialize() {
//        String jsonString = "{\"createTime\":\"2018-08-17 14:38:38\",\"id\":11,\"name\":\"西安\"}";
        ThreatInfo threatInfo = JSON.parseObject(jsonStr, ThreatInfo.class);

        System.out.println( threatInfo.getDstCompany());
    }
}
