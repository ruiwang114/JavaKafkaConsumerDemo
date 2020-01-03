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
import java.util.stream.Stream;

public class RedisTest {

    //时间+行业=key,ip+infoId=分组
//    private static String[] keys=new String[]{"bank","ga"};
    private static String[] keys=new String[]{"yisuo"};

    public static void main(String[] args) {
        addData();
//        getData();
    }

//    @Test
    public static void addData() {
        List list=new ArrayList();
        StringBuilder jsonString=new StringBuilder("[");
//        2000 0 0 0   500k 5m 50m 500m
        for (int i=1 ; i <= 200000*4 ; i++) {
            jsonString.append("{\"dstCompany\":\"一所\",\"endTime\":\"2020-04-11 15:00:00\",\"industryCode\":\"GA1\",\"infoId\":\"18\",\"ip\":\"192.168.1.1\",\"operation\":\"=\",\"score\":\"80\",\"srcCity\":\"天津\",\"srcCountry\":\"中国\",\"srcProvince\":\"天津\",\"startTime\":\"2020-02-01 01:00:00\",\"total\":\"6680\"}");
            if(i!=2000000){
                jsonString.append(",");
            }
        }
        jsonString.append("]");
//        for (int i = 1 ; i <= 20 ; i++){
//            ThreatInfo threatInfo = new ThreatInfo()
//                    .setOperation("=")
//                    .setTotal("6680")
//                    .setScore("80")
//                    .setDstCompany("一所")
//                    .setSrcCountry("中国")
//                    .setSrcProvince("天津")
//                    .setSrcCity("天津");
//            threatInfo.setIp("192.168.1.1");
//            threatInfo.setInfoId("18");
//            threatInfo.setIndustryCode("GA"+i);
//            threatInfo.setStartTime(getPointTime(i,i,i));
//            threatInfo.setEndTime(getPointTime(i+2,i+10,i+14));
//            list.add(threatInfo);
//        }
//        for (int i = 1 ; i <= 10 ; i++){
//            ThreatInfo threatInfo = new ThreatInfo()
//                    .setOperation("=")
//                    .setTotal("6680")
//                    .setScore("80")
//                    .setDstCompany("一所")
//                    .setSrcCountry("中国")
//                    .setSrcProvince("天津")
//                    .setSrcCity("天津");
//            threatInfo.setIp("192.168.1.1");
//            threatInfo.setInfoId("17");
//            threatInfo.setIndustryCode("GA"+i);
//            threatInfo.setStartTime(getPointTime(i,i,i));
//            threatInfo.setEndTime(getPointTime(i+2,i+10,i+14));
//            list.add(threatInfo);
//        }
//        for (int i = 1 ; i <= 10 ; i++){
//            ThreatInfo threatInfo = new ThreatInfo()
//                    .setOperation("=")
//                    .setTotal("6680")
//                    .setScore("80")
//                    .setDstCompany("一所")
//                    .setSrcCountry("中国")
//                    .setSrcProvince("天津")
//                    .setSrcCity("天津");
//            threatInfo.setIp("192.168.1.2");
//            threatInfo.setInfoId("19");
//            threatInfo.setIndustryCode("CA"+i);
//            threatInfo.setStartTime(getPointTime(i,i,i));
//            threatInfo.setEndTime(getPointTime(i+2,i+10,i+14));
//            list.add(threatInfo);
//        }
//        String jsonString = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
        Map<String,String> map=new HashMap<String,String>(){{
            put("1",jsonString.toString());
//            put("2",jsonString);
        }};
        for (String key : keys) {
            RedisUtil.getJedis().hmset(key,map);
        }
    }

//    @Test
    public static void getData(){

        List<ThreatInfo> threatInfos=new ArrayList<ThreatInfo>();
        Integer globalOffset=2;
        List<ThreatInfo> result=new ArrayList<ThreatInfo>();
        //1.按照ip,infoId分组  2.拼接industryCode  3.取最小startTime  4.取最大endTime  5.total求和
        for (String key : keys) {
            Map<String, String> map = RedisUtil.getJedis().hgetAll(key);
            map.entrySet().parallelStream().filter(m -> Integer.parseInt(m.getKey())<=globalOffset).forEach(m -> {
                List<ThreatInfo> threatInfo = JSONArray.parseArray(m.getValue(), ThreatInfo.class);
                threatInfos.addAll(threatInfo);
            });
        }
        Map<String, List<ThreatInfo>> groupResult = threatInfos.stream().collect(Collectors.groupingBy(t -> t.getIp()+"#"+t.getInfoId()));
        groupResult.entrySet().stream().forEach(g -> {
            List<ThreatInfo> threatInfo = g.getValue();
            String industryCodes = threatInfo.stream().map(ThreatInfo::getIndustryCode).distinct().collect(Collectors.joining("|"));
            String startTime = threatInfo.stream().min(Comparator.comparing(ThreatInfo::getStartTime)).get().getStartTime();
            String endTime = threatInfo.stream().max(Comparator.comparing(ThreatInfo::getEndTime)).get().getEndTime();
            long total = threatInfo.stream().mapToLong(t -> Long.parseLong(t.getTotal())).sum();
            ThreatInfo resultThreatInfo=new ThreatInfo()
                    .setIndustryCode(industryCodes)
                    .setStartTime(startTime)
                    .setEndTime(endTime)
                    .setTotal(total+"")
                    .setIp(threatInfo.get(0).getIp())
                    .setInfoId(threatInfo.get(0).getInfoId())
                    .setOperation(threatInfo.get(0).getOperation())
                    .setScore(threatInfo.get(0).getScore())
                    .setDstCompany(threatInfo.get(0).getDstCompany())
                    .setSrcCountry(threatInfo.get(0).getSrcCountry())
                    .setSrcProvince(threatInfo.get(0).getSrcProvince())
                    .setSrcCity(threatInfo.get(0).getSrcCity());
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
