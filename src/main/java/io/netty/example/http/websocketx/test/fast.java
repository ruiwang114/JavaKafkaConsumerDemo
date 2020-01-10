package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import io.netty.example.http.websocketx.entity.G01ThreatInfo;


public class fast {

    private static String jsonStr="";

    public static G01ThreatInfo serialize() {
        G01ThreatInfo g01ThreatInfo = new G01ThreatInfo();
        g01ThreatInfo.setOperation("=");
        g01ThreatInfo.setInfoId(18L);
//        g01ThreatInfo.setIndustryCode("GA");
        g01ThreatInfo.setStartTime("2019-09-10 02:58:55");
        g01ThreatInfo.setEndTime("2019-12-16 15:35:22");
        g01ThreatInfo.setTotal(6680l);
        g01ThreatInfo.setScore(80l);
        g01ThreatInfo.setSrcCountry("中国");
        g01ThreatInfo.setSrcProvince("天津");
        g01ThreatInfo.setSrcCity("天津");
        jsonStr = JSON.toJSONString(g01ThreatInfo);
        System.out.println(jsonStr);
        return g01ThreatInfo;
    }
}
