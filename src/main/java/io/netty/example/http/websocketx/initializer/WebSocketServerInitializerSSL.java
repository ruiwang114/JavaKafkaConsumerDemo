/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.http.websocketx.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.example.http.websocketx.base.Global;
import io.netty.example.http.websocketx.factory.OpenSecureChatSslContextFactory;
import io.netty.example.http.websocketx.handler.WebSocketFrameHandler;
import io.netty.example.http.websocketx.factory.SecureChatSslContextFactory;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.kafka.clients.producer.Producer;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;


/**
 *
 *控制器，帮助用户控制通道
 *
 *@author oldRi
 *Date 20191217
 */
public class WebSocketServerInitializerSSL extends ChannelInitializer<SocketChannel> {

    private static final int READ_IDEL_TIME_OUT=60;
    private static final int WRITE_IDEL_TIME_OUT=30;
    private static final int ALL_IDEL_TIME_OUT=30;


    /**
     * 用于控制wss接口功能
     * example
     * 上传功能url:wss://domain:port/upload
     * 下载功能url:wss://domain:port/download
     */
    private static String WEBSOCKET_PATH = "";

    private Producer<String, String> kafkaProducer;

    public WebSocketServerInitializerSSL(Producer<String, String> kafkaProducer) {
        //kafka生产者对象
        this.kafkaProducer = kafkaProducer;
    }

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
//        engine.setNeedClientAuth();


        //添加SSL认证
        pipeline.addLast("ssl", new SslHandler(engine));
        //HttpServerCodec:将HTTP客户端请求转成HttpRequest对象，将HttpResponse对象编码成HTTP响应发送给客户端
        pipeline.addLast(new HttpServerCodec());
        //请求服务器的时候，对应的参数信息是保存在message body中的,如果只是单纯的用HttpServerCodec
        //是无法完全的解析请求的，因为HttpServerCodec只能获取uri中参数，所以需要加上HttpObjectAggregator.
        pipeline.addLast(new HttpObjectAggregator(6553500));
        //WebSocket数据压缩
        pipeline.addLast(new WebSocketServerCompressionHandler());
        //WebSocket处理器
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true,6553500));
        //心跳设置
//        pipeline.addLast(new IdleStateHandler(READ_IDEL_TIME_OUT,WRITE_IDEL_TIME_OUT,ALL_IDEL_TIME_OUT, TimeUnit.SECONDS));
        //注册Handler
        pipeline.addLast(new WebSocketFrameHandler(kafkaProducer));
    }
}
