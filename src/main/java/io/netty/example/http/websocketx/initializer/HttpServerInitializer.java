package io.netty.example.http.websocketx.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.example.http.websocketx.handler.HttpRequestHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // http 编解码
        pipeline.addLast(new HttpServerCodec());
        // http 消息聚合器512*1024为接收的最大contentlength
        pipeline.addLast("httpAggregator",new HttpObjectAggregator(512*1024));
        // 请求处理器
        pipeline.addLast(new HttpRequestHandler());
    }
}
