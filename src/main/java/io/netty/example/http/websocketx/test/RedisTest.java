package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.netty.example.http.websocketx.entity.G01ThreatInfo;
import io.netty.example.http.websocketx.entity.G01ThreatInfoResp;
import io.netty.example.http.websocketx.enums.K01Enum;
import io.netty.example.http.websocketx.util.DateUtil;
import io.netty.example.http.websocketx.util.PropertyUtil;
import io.netty.example.http.websocketx.util.RedisUtil;
import kafka.utils.json.JsonObject;
import redis.clients.jedis.Jedis;
import sun.security.provider.MD5;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class RedisTest {

//    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    //时间+行业=key,ip+infoId=分组
//    private static String[] keys=new String[]{"bank","ga"};
    private static String[] keys=new String[]{"DX","GD"};
    private static Integer globalOffset=4;

    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {

//        RedisUtil.getJedis().rpush("global_offset","{'global':20200110,'offset':1}","{'global':20200111,'offset':1}");
//        Properties load = PropertyUtil.load("redis.properties");
//        String property = load.getProperty("redis.hostname");
//        System.out.println(property);
//        RedisUtil.getJedis().del("DX");
//        RedisUtil.getJedis().del("GD");
//        addData();
//        getData();
//        List<IndustryInfo> industryInfos = JdbcUtil.queryForList("select * from test;", IndustryInfo.class, null);
//        industryInfos.forEach(System.out::println);
        String s = md5();
        System.out.println(s);
    }

    public static void addData() {
        StringBuilder jsonString=new StringBuilder("[");
//        2000 0 0 0   500k 5m 50m 500m
//        50*3=150M，180，0000条
        LongStream.range(1,2000_00).forEach(l->{
            jsonString.append("{\"dstCompany\":\"一所\",\"endTime\":\"2020-04-11 15:00:00\",\"industryCode\":\"DX\",\"infoId\":16,\"ip\":\"192.168.1.1\",\"operation\":\"=\",\"score\":80,\"srcCity\":\"天津\",\"srcCountry\":\"中国\",\"srcProvince\":\"天津\",\"startTime\":\"2020-02-01 01:00:00\",\"total\":6680}");
            jsonString.append(",{\"dstCompany\":\"一所\",\"endTime\":\"2020-04-11 15:00:00\",\"industryCode\":\"GD\",\"infoId\":16,\"ip\":\"192.168.1.1\",\"operation\":\"=\",\"score\":80,\"srcCity\":\"天津\",\"srcCountry\":\"中国\",\"srcProvince\":\"天津\",\"startTime\":\"2020-02-01 01:00:00\",\"total\":6680}");
            if(l!=2000000){
                jsonString.append(",");
            }
        });
        jsonString.append("]");
        Map<String,String> map=new HashMap<String,String>(){{
            put("1",jsonString.toString());
        }};
        for (String key : keys) {
            RedisUtil.getJedis().hmset(key,map);
        }
    }

    public static void getData(){
        Long sTime=System.currentTimeMillis();
        List<G01ThreatInfo> g01ThreatInfos =new ArrayList<>();
        Jedis jedis = RedisUtil.getJedis();
        //1.按照ip,infoId分组  2.拼接industryCode  3.取最小startTime  4.取最大endTime  5.total求和
        for (String key : keys) {
//            Map<String, String> map = RedisUtil.getJedis().hgetAll(key);
//            map.entrySet().parallelStream().filter(m -> Integer.parseInt(m.getKey())<=globalOffset).forEach(m -> {
//                List<ThreatInfo> threatInfo = JSONArray.parseArray(m.getValue(), ThreatInfo.class);
//                threatInfos.addAll(threatInfo);
//            });
            for(int i = 1 ; i <= globalOffset ; i++){
                String value = jedis.hget(key, i+"");
                List<G01ThreatInfo> g01ThreatInfo = JSONArray.parseArray(value, G01ThreatInfo.class);
                if(g01ThreatInfo ==null) continue;
//                executorService.execute(() -> {
                    List<G01ThreatInfo> dispose = dispose(g01ThreatInfo);
                    g01ThreatInfos.addAll(dispose);
//                });
            }
        }
        List<G01ThreatInfoResp> result=new ArrayList<>();
        List<G01ThreatInfo> dispose = dispose(g01ThreatInfos);
        dispose.parallelStream().forEach(d -> {
            String[] split = d.getIndustryCode().split(",");
            List<String> strings = Arrays.asList(split);
            G01ThreatInfoResp resp=G01ThreatInfoResp.builder()
                    .ip(d.getIp()).infoId(d.getInfoId()).endTime(d.getEndTime())
                    .startTime(d.getStartTime()).industryCode(strings).operation(d.getOperation())
                    .score(d.getScore()).srcCity(d.getSrcCity()).srcCountry(d.getSrcCountry())
                    .srcProvince(d.getSrcProvince()).total(d.getTotal()).build();
            result.add(resp);
        });
        result.stream().forEach(System.out::println);
        Long eTime=System.currentTimeMillis();
        System.out.println("耗时："+(eTime-sTime));

    }

    public static List<G01ThreatInfo> dispose(List<G01ThreatInfo> g01ThreatInfos){

        List<G01ThreatInfo> result=new ArrayList<>();
        Map<String, List<G01ThreatInfo>> groupResult = g01ThreatInfos.parallelStream().collect(Collectors.groupingBy(t -> t.getIp()+"#"+t.getInfoId()));
        groupResult.entrySet().parallelStream().forEach(g -> {
            List<G01ThreatInfo> g01ThreatInfo = g.getValue();
            String industryCodes = g01ThreatInfo.parallelStream().map(G01ThreatInfo::getIndustryCode).distinct().collect(Collectors.joining(","));
            String startTime = g01ThreatInfo.parallelStream().min(Comparator.comparing(G01ThreatInfo::getStartTime)).get().getStartTime();
            String endTime = g01ThreatInfo.parallelStream().max(Comparator.comparing(G01ThreatInfo::getEndTime)).get().getEndTime();
            long total = g01ThreatInfo.parallelStream().mapToLong(t -> t.getTotal()).sum();
            G01ThreatInfo resultG01ThreatInfo =new G01ThreatInfo()
            .setIndustryCode(industryCodes)
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setTotal(total)
            .setIp(g01ThreatInfo.get(0).getIp())
            .setInfoId(g01ThreatInfo.get(0).getInfoId())
            .setOperation(g01ThreatInfo.get(0).getOperation())
            .setScore(g01ThreatInfo.get(0).getScore())
            .setSrcCountry(g01ThreatInfo.get(0).getSrcCountry())
            .setSrcProvince(g01ThreatInfo.get(0).getSrcProvince())
            .setSrcCity(g01ThreatInfo.get(0).getSrcCity());
            result.add(resultG01ThreatInfo);
        });
        return result;
    }

    /**
     * 获取指定的时间
     *
     * @Date 2019-09-08 20:52
     **/
    public static String getPointTime(int month,int day, int hour){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format (cal.getTime());
    }

    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            map.put(fieldName, field.get(obj)!=null?field.get(obj).toString():"");
        }
        return map;
    }

    public static String md5() throws NoSuchAlgorithmException {
        MessageDigest m= MessageDigest.getInstance("MD5");
        m.update("k01WebSocket-redis".getBytes());
        byte[] s = m.digest();
        String result="";
        for (int i=0; i<s.length;i++){
            result+=Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
        }
        return result;
    }
}
