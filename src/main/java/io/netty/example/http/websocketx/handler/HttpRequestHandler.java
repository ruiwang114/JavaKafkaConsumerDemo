package io.netty.example.http.websocketx.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.websocketx.base.ResultData;
import io.netty.example.http.websocketx.constant.SqlDefine;
import io.netty.example.http.websocketx.entity.*;
import io.netty.example.http.websocketx.exception.BizException;
import io.netty.example.http.websocketx.util.DbUtil;
import io.netty.example.http.websocketx.util.RedisUtil;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req){

        Jedis jedis = RedisUtil.getJedis();
        ResultData result=new ResultData();
        try {

            String uri = req.getUri();
            System.out.println(uri);
            String authCode="authcodeHou";

            //globalOffset >= reqOffset ： 全量offset=globalOffset; globalOffset < reqOffset ： 全量offset=reqOffset
            Integer globalOffset=DownWebSocketFrameHandler.getGlobalOffset(jedis);
            boolean isGlobal = true;
            Integer offset =  globalOffset;

            //用户校验
            Object consumerId = DownWebSocketFrameHandler.checkConsumer(authCode);

            //Tencent情报聚合（全量时返回）
            List<T01ThreatInfoResp> t01ThreatInfoResp=new ArrayList<>();
            if(isGlobal){
                t01ThreatInfoResp =DownWebSocketFrameHandler.t01Aggregate(consumerId);
            }

            //设备行业信息查询
            List<Object> industryCodes = DbUtil.listHandler(SqlDefine.SELECT_G01_INDUSTRY, null,  consumerId);

            //G01情报聚合
            List<G01ThreatInfo> g01ThreatInfos = DownWebSocketFrameHandler.g01AggregateByIndustry(industryCodes, offset,jedis);
            //G01数据格式转换
            List<G01ThreatInfoResp> g01ThreatInfoResp = DownWebSocketFrameHandler.g01ConvertData(g01ThreatInfos);

            //封装聚合数据
            ThreatDownResp resp=new ThreatDownResp()
                    .setNew_offset(offset+1)
                    .setIs_global(isGlobal ? 1 : 0)
                    .setG01(g01ThreatInfoResp)
                    .setT01(t01ThreatInfoResp);

            result.setMsg(resp);
            // 创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue), CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }catch (Exception e){
            log.error("DownWebSocketFrameHandler ERROR : {}",e);
            result.setStatus(false);
            if(e instanceof BizException){
                result.setHint(((BizException) e).getErrorMsg());
            }else{
                result.setHint("ERROR");
            }
            // 创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue), CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }finally {
            //释放连接
            RedisUtil.releaseResource(jedis);
        }
    }
}
