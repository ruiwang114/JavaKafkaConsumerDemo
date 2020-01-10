package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import io.netty.example.http.websocketx.entity.G01ThreatInfo;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class grouping {
    public static void main(String[] args) {
        Jedis jRedis= RedisUtilTest.getJedis();

        List<G01ThreatInfo> threatList=new ArrayList<G01ThreatInfo>();

        // 取出redis中的map进行遍历
        Map<String, String> userMap = jRedis.hgetAll("GA");
        for (Map.Entry<String, String> item : userMap.entrySet()) {
            System.out.println(item.getKey() + " : " + item.getValue());
//            System.out.println(item.getValue());
            G01ThreatInfo g01ThreatInfo = JSON.parseObject(item.getValue(), G01ThreatInfo.class);
            threatList.add(g01ThreatInfo);
        }

        Map<Long, Long> map = threatList.stream().
                collect(Collectors.groupingBy(G01ThreatInfo::getInfoId,Collectors.counting()));

        System.out.printf(map.toString());
    }
}
