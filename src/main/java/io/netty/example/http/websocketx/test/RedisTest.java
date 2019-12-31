package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.example.http.websocketx.entity.ThreatInfo;
import io.netty.example.http.websocketx.util.RedisUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class RedisTest {

    //时间+行业=key,ip+infoId=分组
    private static String key="2019-12-30:bank";

    public static void main(String[] args) {
        getData();
    }

//    @Test
    public void addData() throws IllegalAccessException {
        List list=new ArrayList();
        for (int i = 1 ; i <= 10 ; i++){
            ThreatInfo threatInfo = new ThreatInfo()
                    .setOperation("=")
                    .setTotal("6680")
                    .setScore("80")
                    .setDstCompany("一所")
                    .setSrcCountry("中国")
                    .setSrcProvince("天津")
                    .setSrcCity("天津");
            threatInfo.setIp("192.168.1.1");
            threatInfo.setInfoId("18");
            threatInfo.setIndustryCode("GA"+i);
            threatInfo.setStartTime(getPointTime(i,i,i));
            threatInfo.setEndTime(getPointTime(i+2,i+10,i+14));
            list.add(threatInfo);
        }
        for (int i = 1 ; i <= 10 ; i++){
            ThreatInfo threatInfo = new ThreatInfo()
                    .setOperation("=")
                    .setTotal("6680")
                    .setScore("80")
                    .setDstCompany("一所")
                    .setSrcCountry("中国")
                    .setSrcProvince("天津")
                    .setSrcCity("天津");
            threatInfo.setIp("192.168.1.1");
            threatInfo.setInfoId("17");
            threatInfo.setIndustryCode("GA"+i);
            threatInfo.setStartTime(getPointTime(i,i,i));
            threatInfo.setEndTime(getPointTime(i+2,i+10,i+14));
            list.add(threatInfo);
        }
        for (int i = 1 ; i <= 10 ; i++){
            ThreatInfo threatInfo = new ThreatInfo()
                .setOperation("=")
                .setTotal("6680")
                .setScore("80")
                .setDstCompany("一所")
                .setSrcCountry("中国")
                .setSrcProvince("天津")
                .setSrcCity("天津");
            threatInfo.setIp("192.168.1.2");
            threatInfo.setInfoId("19");
            threatInfo.setIndustryCode("CA"+i);
            threatInfo.setStartTime(getPointTime(i,i,i));
            threatInfo.setEndTime(getPointTime(i+2,i+10,i+14));
            list.add(threatInfo);
        }
        String jsonString = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
        Map<String,String> map=new HashMap<String,String>(){{
            put("2",jsonString);
        }};
        RedisUtil.getJedis().hmset(key,map);
    }

//    @Test
    public static void getData(){

        List<ThreatInfo> result=new ArrayList<>();
        //根据请求参数得知redisKey+offset
        //1.按照ip,infoId分组  2.拼接industryCode  3.取最小startTime  4.取最大endTime
        String str = RedisUtil.getJedis().hget(key, "2");
        List<ThreatInfo> threatInfos = JSONArray.parseArray(str, ThreatInfo.class);
        Map<String, List<ThreatInfo>> groupResult = threatInfos.stream().collect(Collectors.groupingBy(t -> t.getIp()+"#"+t.getInfoId()));
        groupResult.entrySet().stream().forEach(g -> {
//            System.out.println("collect key : "+ g.getKey());
            List<ThreatInfo> threatInfo = g.getValue();
            ThreatInfo resultThreatInfo=new ThreatInfo();
            String industryCodes = threatInfo.stream().map(ThreatInfo::getIndustryCode).collect(Collectors.joining(",","[","]"));
//            System.out.println("industryCodes : "+ industryCodes);
            resultThreatInfo.setIndustryCode(industryCodes);
            String startTime = threatInfo.stream().min(Comparator.comparing(ThreatInfo::getStartTime)).get().getStartTime();
//            System.out.println("startTime : "+startTime);
            resultThreatInfo.setStartTime(startTime);
            String endTime = threatInfo.stream().max(Comparator.comparing(ThreatInfo::getEndTime)).get().getEndTime();
//            System.out.println("endTime : "+endTime);
            resultThreatInfo.setEndTime(endTime);
            result.add(resultThreatInfo);
        });
        result.stream().forEach(System.out::println);
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

}
