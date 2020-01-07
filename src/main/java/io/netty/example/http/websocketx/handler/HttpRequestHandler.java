package io.netty.example.http.websocketx.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.websocketx.entity.ResultData;
import io.netty.example.http.websocketx.entity.ThreatInfo;
import io.netty.example.http.websocketx.util.RedisUtil;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req){

        ResultData result=new ResultData();
        Jedis jedis = RedisUtil.getJedis();
        try {
            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.CONTINUE));
            }
            Integer offset=2;
            String[] keys=new String[]{"bank","ga"};
            List<ThreatInfo> threatInfos = DownWebSocketFrameHandler.aggregateByOffset(keys, offset,jedis);
            threatInfos = DownWebSocketFrameHandler.dataAggregate(threatInfos);
            JSONObject rs=new JSONObject();
            rs.put("new_offset",offset+1);
            rs.put("threat_info", threatInfos);
            result.setMsg(rs);
            // 创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(JSON.toJSONString(result), CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }catch (Exception e){
            result.setStatus(0);
            // 创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(JSON.toJSONString(result), CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }finally {
            RedisUtil.releaseResource(jedis);
        }
    }
}
