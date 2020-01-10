package io.netty.example.http.websocketx.handler;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.websocketx.base.ResultData;
import io.netty.example.http.websocketx.constant.Hints;
import io.netty.example.http.websocketx.constant.SqlDefine;
import io.netty.example.http.websocketx.entity.*;
import io.netty.example.http.websocketx.enums.K01Enum;
import io.netty.example.http.websocketx.exception.BizException;
import io.netty.example.http.websocketx.util.DateUtil;
import io.netty.example.http.websocketx.util.DbUtil;
import io.netty.example.http.websocketx.util.RedisUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 相应客户端的request
 *
 * @author oldRi
 * Date 20191226
 */
@Slf4j
public class DownWebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {

        if (frame instanceof TextWebSocketFrame) {
            Jedis jedis = RedisUtil.getJedis();
            ResultData result=new ResultData();
            try {
                String request = ((TextWebSocketFrame) frame).text();
                ThreatDownReq threatDownReq = JSON.parseObject(request, ThreatDownReq.class);

                //globalOffset >= reqOffset ： offset=globalOffset; globalOffset < reqOffset ： offset=reqOffset
                Integer globalOffset=getGlobalOffset(jedis);
                boolean isGlobal = globalOffset >= threatDownReq.getOffset();
                Integer offset =  isGlobal ? globalOffset : threatDownReq.getOffset();

                //用户校验
                Object consumerId = checkConsumer(threatDownReq.getSerial_num());

                //Tencent情报聚合（全量时返回）
                List<T01ThreatInfoResp> t01ThreatInfoResp=new ArrayList<>();
                if(isGlobal){
                    t01ThreatInfoResp = t01Aggregate(consumerId);
                }

                //设备行业信息查询
                List<Object> industryCodes = DbUtil.listHandler(SqlDefine.SELECT_G01_INDUSTRY, null,  consumerId);

                //G01情报聚合
                List<G01ThreatInfo> g01ThreatInfos = g01AggregateByIndustry(industryCodes, offset,jedis);
                //G01数据格式转换
                List<G01ThreatInfoResp> g01ThreatInfoResp = g01ConvertData(g01ThreatInfos);

                //封装聚合数据
                ThreatDownResp resp=new ThreatDownResp()
                        .setNew_offset(g01ThreatInfoResp.isEmpty() ? offset : offset+1)
                        .setIs_global(isGlobal ? 1 : 0)
                        .setG01(g01ThreatInfoResp)
                        .setT01(t01ThreatInfoResp);

                result.setMsg(resp);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue)));
            }catch (Exception e){
                log.error("DownWebSocketFrameHandler ERROR : {}",e);
                result.setStatus(false);
                if(e instanceof BizException){
                    result.setHint(((BizException) e).getErrorMsg());
                }else{
                    result.setHint(Hints.SYSTEM_ERROR);
                }
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(result)));
            }finally {
                //释放连接
                RedisUtil.releaseResource(jedis);
            }
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    /**
     * 用户校验
     * @param authCode
     * @return
     * @throws SQLException
     * @throws BizException
     * @throws ParseException
     */
    public static Object checkConsumer(String authCode) throws SQLException, BizException {
        Map<String, Object> map = DbUtil.mapHandler(SqlDefine.SELECT_CONSUMER_BY_AUTHKEYCODE, authCode);
        if(!map.isEmpty()){
            Object consumerId = map.get(K01Enum.SqlFields.CONSUMER_ID.name());
            Object status = map.get(K01Enum.SqlFields.STATUS.name());
            Object expireTime = map.get(K01Enum.SqlFields.EXPIRE_TIME.name());
            LocalDateTime expireDateTime = DateUtil.stringToDate(expireTime.toString(), DateUtil.DATE_TIME_PATTERN);
            if(!status.equals("0"))//0正常 1冻结
                throw new BizException(Hints.CONSUMER_STATUS_FREEZE);
            if(expireDateTime.compareTo(LocalDateTime.now()) < 0)
                throw new BizException(Hints.CONSUMER_SERVICE_EXPIRE);
            return consumerId;
        }
        return new BizException(Hints.CONSUMER_NOT_EXIST);
    }

    /**
     * T01情报数据聚合
     * @param consumerId
     * @throws SQLException
     */
    public static List<T01ThreatInfoResp> t01Aggregate(Object consumerId) throws SQLException {
        List<T01ThreatInfoResp> list=new CopyOnWriteArrayList<>();
        //查询用户与腾讯的情报绑定关系
        Object result = DbUtil.scalarHandler(SqlDefine.SELECT_CONSUMER_TENCENT_RELATION, consumerId);
        if(result != null){
            //查询腾讯情报数据
            List<T01ThreatInfo> t01ThreatInfoResps = DbUtil.beanListHander(SqlDefine.SELECT_TENCENT_THREAT_DATA, T01ThreatInfo.class);
            t01ThreatInfoResps.parallelStream().forEach(t -> {
                String[] tagList = t.getTAG_LIST().split(K01Enum.SpecialCharacters.UPRIGHT_LINE_TRAN.getValue());
                String[] threatList = t.getTHREAT_LIST().split(K01Enum.SpecialCharacters.UPRIGHT_LINE_TRAN.getValue());
                T01ThreatInfoResp resp = new T01ThreatInfoResp()
                        .setInfoName(t.getNAME()).setInfoType(t.getTYPE()).setLevel(t.getLEVEL())
                        .setConfidence(t.getCONFIDENCE()).setStatus(t.getACTIVE_STATUE())
                        .setSubFlag(t.getDOMAIN_FLAG()).setTagList(Arrays.asList(tagList)).setThreatList(Arrays.asList(threatList))
                        .setUpdateTime(t.getUPDATE_TIME());
                list.add(resp);
            });
        }
        return list;

    }

    /**
     * G01聚合数据格式转换
     * @return
     */
    public static List<G01ThreatInfoResp> g01ConvertData(List<G01ThreatInfo> g01ThreatInfos){
        List<G01ThreatInfoResp> result=new CopyOnWriteArrayList<>();
        g01ThreatInfos.parallelStream().forEach(d -> {
            String[] split = d.getIndustryCode().split(K01Enum.SpecialCharacters.UPRIGHT_LINE_TRAN.getValue());
            List<String> strings = Arrays.asList(split);
            G01ThreatInfoResp resp=G01ThreatInfoResp.builder()
                    .ip(d.getIp()).infoId(d.getInfoId()).endTime(d.getEndTime())
                    .startTime(d.getStartTime()).industryCode(strings).operation(d.getOperation())
                    .score(d.getScore()).srcCity(d.getSrcCity()).srcCountry(d.getSrcCountry())
                    .srcProvince(d.getSrcProvince()).total(d.getTotal()).build();
            result.add(resp);
        });
        return result;
    }

    /**
     * 按照offset处理情报数据
     * @param keys 行业key数组
     * @param offset offset值
     * @return
     */
    public static List<G01ThreatInfo> g01AggregateByIndustry(List<Object> keys, Integer offset, Jedis jedis){
        List<G01ThreatInfo> g01ThreatInfos =new ArrayList<>();
        for (Object key : keys) {
//            for(int i = 1 ; i <= offset ; i++){
                String value = jedis.hget(key.toString(), offset.toString());
                if(StringUtils.isEmpty(value)) continue;
                List<G01ThreatInfo> g01ThreatInfo = JSONArray.parseArray(value, G01ThreatInfo.class);
                if(g01ThreatInfo.isEmpty()) continue;
                List<G01ThreatInfo> dispose = g01Aggregate(g01ThreatInfo);
                g01ThreatInfos.addAll(dispose);
//            }
        }
        return g01ThreatInfos;
    }
    /**
     * 聚合情报数据
     * @param g01ThreatInfos
     * @return
     */
    public static List<G01ThreatInfo> g01Aggregate(List<G01ThreatInfo> g01ThreatInfos){

        List<G01ThreatInfo> result=new CopyOnWriteArrayList<>();
        try {
            //1.按照ip,infoId分组  2.拼接industryCode  3.取最小startTime  4.取最大endTime  5.total求和
            Map<String, List<G01ThreatInfo>> groupResult = g01ThreatInfos.parallelStream().collect(Collectors.groupingBy(t -> t.getIp()+"#"+t.getInfoId()));
            groupResult.entrySet().parallelStream().forEach(g -> {
                List<G01ThreatInfo> g01ThreatInfo = g.getValue();
                String industryCodes = g01ThreatInfo.parallelStream().map(G01ThreatInfo::getIndustryCode).distinct().collect(Collectors.joining(K01Enum.SpecialCharacters.UPRIGHT_LINE.getValue()));
                String startTime = g01ThreatInfo.parallelStream().min(Comparator.comparing(G01ThreatInfo::getStartTime)).get().getStartTime();
                String endTime = g01ThreatInfo.parallelStream().max(Comparator.comparing(G01ThreatInfo::getEndTime)).get().getEndTime();
                long total = g01ThreatInfo.parallelStream().mapToLong(G01ThreatInfo::getTotal).sum();
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
        }catch (Exception e){
            log.error("DownWebSocketFrameHandler-DataAggregate : {}",e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取全局offset
     * @return
     */
    public static Integer getGlobalOffset(Jedis jedis){

        Integer globalOffset=0;
        try {
            String nowDate = DateUtil.dateToString(new Date(), DateUtil.DATE_PATTERN);
            List<String> globalList = jedis.lrange(K01Enum.CacheFields.global_offset.name(), 0, -1);
            for (String globalJson : globalList) {
                JSONObject jsonObject = JSON.parseObject(globalJson);
                if(jsonObject.getLongValue(K01Enum.CacheFields.global.name()) == Long.parseLong(nowDate)){
                    globalOffset=jsonObject.getInteger(K01Enum.CacheFields.offset.name());
                    break;
                }
            }
        }catch (Exception e){
            log.error("DownWebSocketFrameHandler-GetGlobalOffset : {}",e);
            e.printStackTrace();
        }
        return globalOffset;
    }
}
