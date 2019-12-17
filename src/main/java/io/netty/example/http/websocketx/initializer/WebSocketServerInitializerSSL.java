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
import io.netty.example.http.websocketx.handler.WebSocketFrameHandler;
import io.netty.example.http.websocketx.factory.SecureChatSslContextFactory;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;
import org.apache.kafka.clients.producer.Producer;
import javax.net.ssl.SSLEngine;


/**
 *
 *控制器，帮助用户控制通道
 *
 *@author oldRi
 *Date 20191217
 */
public class WebSocketServerInitializerSSL extends ChannelInitializer<SocketChannel> {

    /**
     * 用于控制wss接口功能
     * example
     * 上传功能url:wss://domain:port/upload
     * 下载功能url:wss://domain:port/download
     */
    private static String WEBSOCKET_PATH = "";

    private Producer<String, String> kafkaProducer;

    public WebSocketServerInitializerSSL(Producer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        WEBSOCKET_PATH= Global.websocketPath;
        //获取证书路径
        String sChatPath =  Global.serverKeyStorePath;
        //使用SSL、双向认证
        SSLEngine engine = SecureChatSslContextFactory.getServerContext(sChatPath,sChatPath).createSSLEngine();
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
        pipeline.addLast(new WebSocketFrameHandler(kafkaProducer));
    }
}