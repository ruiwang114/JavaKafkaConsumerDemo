package io.netty.example.http.websocketx.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.websocketx.entity.ThreatDownReq;
import io.netty.example.http.websocketx.entity.ThreatInfo;
import io.netty.example.http.websocketx.util.RedisUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 相应客户端的request
 *
 * @author oldRi
 * Date 20191226
 */
@Slf4j
public class DownWebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private Jedis jRedis;;

    public DownWebSocketFrameHandler(Jedis jRedis) {
        this.jRedis = jRedis;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            System.out.println(request);
            ThreatDownReq threatDownReq = JSON.parseObject(request, ThreatDownReq.class);
            //根据reqOffset判断offset: reqOffset<=globalOffset->globalOffset;reqOffset>globalOffset->reqOffset;
            Integer globalOffset=getGlobalOffset();
            Integer offset = Integer.parseInt(threatDownReq.getOffset()) > globalOffset ? Integer.parseInt(threatDownReq.getOffset()) : globalOffset;
            //根据序列号信息获取行业信息key
            String[] keys=new String[]{"bank","ga"};
            List<ThreatInfo> threatInfos = dataAggregate(keys, offset);
            JSONObject response=new JSONObject();
            response.put("new_offset",offset+1);
            response.put("threat_info",JSON.toJSONString(threatInfos));
            ctx.channel().writeAndFlush(new TextWebSocketFrame(response.toJSONString()));
            //释放连接
            RedisUtil.releaseResource(jRedis);
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    /**
     * 聚合情报数据
     * @param keys 行业key数组
     * @param offset offset值
     * @return
     */
    public static List<ThreatInfo> dataAggregate(String[] keys,Integer offset){

        Jedis jedis = RedisUtil.getJedis();
        List<ThreatInfo> result=new ArrayList<>();
        try {
            List<ThreatInfo> threatInfos=new ArrayList<>();
            //1.按照ip,infoId分组  2.拼接industryCode  3.取最小startTime  4.取最大endTime  5.total求和
            for (String key : keys) {
                Map<String, String> map = jedis.hgetAll(key);
                map.entrySet().parallelStream().filter(m -> Integer.parseInt(m.getKey())<=offset).forEach(m -> {
                    List<ThreatInfo> threatInfo = JSONArray.parseArray(m.getValue(), ThreatInfo.class);
                    threatInfos.addAll(threatInfo);
                });
            }
            Map<String, List<ThreatInfo>> groupResult = threatInfos.parallelStream().collect(Collectors.groupingBy(t -> t.getIp()+"#"+t.getInfoId()));
            groupResult.entrySet().parallelStream().forEach(g -> {
                List<ThreatInfo> threatInfo = g.getValue();
                String industryCodes = threatInfo.parallelStream().map(ThreatInfo::getIndustryCode).distinct().collect(Collectors.joining("|"));
                String startTime = threatInfo.parallelStream().min(Comparator.comparing(ThreatInfo::getStartTime)).get().getStartTime();
                String endTime = threatInfo.parallelStream().max(Comparator.comparing(ThreatInfo::getEndTime)).get().getEndTime();
                long total = threatInfo.parallelStream().mapToLong(t -> Long.parseLong(t.getTotal())).sum();
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
        }catch (Exception e){
            log.error("DownWebSocketFrameHandler-DataAggregate : {}",e);
            e.printStackTrace();
        }finally {
            RedisUtil.releaseResource(jedis);
        }
        return result;
    }

    /**
     * 获取全局offset
     * @return
     */
    public Integer getGlobalOffset(){

        Jedis jedis=RedisUtil.getJedis();
        Integer globalOffset=0;
        try {
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ISO_LOCAL_DATE;
            String date = LocalDate.now().format(dateTimeFormatter);
            String s = jedis.get(date);
            globalOffset=Integer.parseInt(s);
        }catch (Exception e){
            log.error("DownWebSocketFrameHandler-GetGlobalOffset : {}",e);
            e.printStackTrace();
        }finally {
            RedisUtil.releaseResource(jedis);
        }
        return 2;
    }
}
