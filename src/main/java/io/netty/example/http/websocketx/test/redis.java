package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.example.http.websocketx.test.fast.serialize;

@Slf4j
public class redis {

    private  static String MapName="";

    public static  Map<String, String> setMap(String offset,String threatJson){

        Map<String, String> map = new HashMap();
        map.put(offset, threatJson);
//        map.put("threatInfo", threatJson);
        return  map;
    }

    public static void main(String[] args) {
        ThreatInfo tInfo=serialize();

        MapName=tInfo.getIndustryCode();

        String jsonStr= JSON.toJSONString(tInfo);

        for(int i=0;i<=49;i++){
            setMap(String.valueOf(i),jsonStr);
        }

        //连接本地的 Redis 服务
//        RedisUtil.getJedis();
//        RedisUtil.releaseResource(RedisUtil.getJedis());
        Jedis jRedis=RedisUtil.getJedis();

        // 将map存入redis中
        jRedis.hmset(MapName, setMap("1",jsonStr));
        jRedis.hmset(MapName, setMap("2",jsonStr));


        // 取出redis中的map进行遍历
        Map<String, String> userMap = jRedis.hgetAll(MapName);
        for (Map.Entry<String, String> item : userMap.entrySet()) {
//            System.out.println(item.getKey() + " : " + item.getValue());
            System.out.println(item);
        }

//        List<String> hmget=jRedis
//        for (String str:hmget) {
//            System.out.println(str);
//        }

    }

}
