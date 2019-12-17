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
import io.netty.example.http.websocketx.factory.OldSecureChatSslContextFactory;
import io.netty.example.http.websocketx.handler.WebSocketFrameHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;
import org.apache.kafka.clients.producer.Producer;

import javax.net.ssl.SSLEngine;

/**
 */
public class OldWebSocketServerInitializerSSL extends ChannelInitializer<SocketChannel> {

    private static final String WEBSOCKET_PATH = "/websocket";

    private Producer<String, String> kafkaProducer;

    public OldWebSocketServerInitializerSSL(Producer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        if (sslCtx != null) {
//            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
//        }
        String sChatPath = (System.getProperty("user.dir")+ "/src/main/java/io/netty/example/http/websocketx/conf/twoway/sChat.jks");

        SSLEngine engine = OldSecureChatSslContextFactory.getServerContext(sChatPath,sChatPath).createSSLEngine();
        engine.setUseClientMode(false);//设置服务端模式
        engine.setNeedClientAuth(true);//需要客户端验证
//        try{
//            System.out.println("判定kafka producer连接状态过程中...");
//            ProducerRecord<String, String> record = new ProducerRecord<String, String>("test1205", "1");
//            kafkaProducer.send(record).get();
//            System.out.println("kafka producer连接正常");
//        }
//        catch (Exception err){
//            err.printStackTrace();
//            System.out.println("kafka producer连接异常，重新初始化连接");
//            kafkaProducer=InitConnect();
//        }
        pipeline.addLast("ssl", new SslHandler(engine));
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
//        pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketFrameHandler(kafkaProducer));
    }
}
