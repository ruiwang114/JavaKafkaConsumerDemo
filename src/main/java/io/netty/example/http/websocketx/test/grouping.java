package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class grouping {
    public static void main(String[] args) {
        Jedis jRedis=RedisUtil.getJedis();

        List<ThreatInfo> threatList=new ArrayList<ThreatInfo>();

        // 取出redis中的map进行遍历
        Map<String, String> userMap = jRedis.hgetAll("GA");
        for (Map.Entry<String, String> item : userMap.entrySet()) {
            System.out.println(item.getKey() + " : " + item.getValue());
//            System.out.println(item.getValue());
            ThreatInfo threatInfo = JSON.parseObject(item.getValue(), ThreatInfo.class);
            threatList.add(threatInfo);
        }

        Map<String, Long> map = threatList.stream().
                collect(Collectors.groupingBy(ThreatInfo::getInFold,Collectors.counting()));

        System.out.printf(map.toString());
    }
}