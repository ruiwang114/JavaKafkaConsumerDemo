package io.netty.example.http.websocketx.handler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.websocketx.base.Global;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;


import static io.netty.example.http.websocketx.kafkaproducer.KafkaClient.kafkaSend;

/**
 * Netty websocket handler.
 *
 * @author wr
 * @date 20200316
 */
@Slf4j
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private Producer<String, String> kafkaProducer;

    public WebSocketFrameHandler(Producer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        try {
            if (frame instanceof TextWebSocketFrame) {
                //获取当前请求内容字符串
                String request = ((TextWebSocketFrame) frame).text();
                //转为json
                JSONObject json = JSONObject.parseObject(request);
                JSONArray jsonArray = json.getJSONArray("data");
                String company=json.getString("company");
                JSONObject topicJson=JSONObject.parseObject(Global.topics);
                if(!topicJson.containsKey(company)){
                    System.out.println("company code error");
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"400\",\"msg\":\"company code error\"}"));
                }else {//获取Json中data数组，数组内为K01日志
                    //遍历K01日志，并写入kafka
                    for (int i = 0; i < jsonArray.size(); i++) {
                        json = JSONObject.parseObject(jsonArray.getString(i));
                        kafkaSend(kafkaProducer, json.toJSONString(), company);
                    }
                    //写入完成后，返回success
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"200\",\"msg\":\"success\"}"));
                }

//            System.out.println(request);
            } else {
                String message = "unsupported frame type: " + frame.getClass().getName();
                throw new UnsupportedOperationException(message);
            }
        }catch (Exception err){
            ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"400\",\"msg\":\""+err+"\"}"));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"400\",\"msg\":\""+cause.toString()+"\"}"));

        ctx.close();
    }
}
