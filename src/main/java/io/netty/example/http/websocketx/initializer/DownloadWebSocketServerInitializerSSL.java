package io.netty.example.http.websocketx.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.example.http.websocketx.base.Global;
import io.netty.example.http.websocketx.factory.OpenSecureChatSslContextFactory;
import io.netty.example.http.websocketx.handler.DownWebSocketFrameHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;


/**
 *
 *下拉工程控制器，帮助用户控制通道
 *
 *@author oldRi
 *Date 20191226
 */
public class DownloadWebSocketServerInitializerSSL extends ChannelInitializer<SocketChannel> {

    /**
     * 用于控制wss接口功能
     * example
     * 上传功能url:wss://domain:port/upload
     * 下载功能url:wss://domain:port/download
     */
    private static String WEBSOCKET_PATH = "";

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        WEBSOCKET_PATH= Global.websocketPath;
        //获取服务端证书路径
        String sChatPath =  Global.serverKeyStorePath;
        //获取根证书路径
        String sTrustPath = Global.rootKeyStorePath;
        //使用SSL、双向认证
        SSLEngine engine = OpenSecureChatSslContextFactory.getServerContext(sChatPath,sTrustPath).createSSLEngine();
        //设置服务端模式
        engine.setUseClientMode(false);
        //需要客户端验证
        engine.setNeedClientAuth(true);

        pipeline.addLast("ssl", new SslHandler(engine));
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        //Handler
        pipeline.addLast(new DownWebSocketFrameHandler());
    }
}
