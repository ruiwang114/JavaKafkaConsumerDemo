package io.netty.example.http.websocketx.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.websocketx.util.RedisUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import redis.clients.jedis.Jedis;

import java.util.Locale;

/**
 * 相应客户端的request
 *
 * @author oldRi
 * Date 20191226
 */
public class DownWebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    //redis连接
    private Jedis jRedis;;

    public DownWebSocketFrameHandler(Jedis jRedis) {
        this.jRedis = jRedis;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            //获取到客户端上传的字符串
            String request = ((TextWebSocketFrame) frame).text();
            //根据请求参数聚合数据返回给客户端


            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
            System.out.println(request);
            //释放连接
            RedisUtil.releaseResource(jRedis);
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }
}
