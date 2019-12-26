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
package io.netty.example.http.websocketx.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.http.websocketx.base.Global;
import io.netty.example.http.websocketx.initializer.WebSocketServerInitializerSSL;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;

import static io.netty.example.http.websocketx.kafkaproducer.KafkaClient.InitConnect;


/**
 *
 * 工程主类,包含工程启动主函数.
 *
 *
 * @author oldRi
 * Date 20191217
 */
@Slf4j
public final class WebSocketServerSSL {

    //加载入口参数
//    static final boolean SSL = System.getProperty("ssl") != null;
//    static final int PORT = Integer.pars、eInt(System.getProperty("port", SSL? "8443" : "8080"));
    static Producer<String, String> kafkaProducer=null;

    public static void main(String[] args) throws Exception {
        WssServerStart();
    }

    /**
     * 启动netty.
     * @param
     * @return
     * @throws InterruptedException
     */
    public static void WssServerStart(){
        //初始化Kafka生产者
        kafkaProducer=InitConnect();
        //初始化服务绑定端口
        int wssServicePort= Global.wssServicePort;
        //bossGroup 线程池则只是在 Bind 某个端口后，获得其中一个线程作为 MainReactor，专门处理端口的 Accept 事件，
        //每个端口对应一个 Boss 线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //workerGroup 线程池会被各个 SubReactor 和 Worker 线程充分利用
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //服务端启动引导类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // 设置channel类型为NIO类型
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebSocketServerInitializerSSL(kafkaProducer));

            Channel ch = b.bind(wssServicePort).sync().channel();

            log.info("wss服务已在本地端口： " + wssServicePort + "绑定并启动");

            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            kafkaProducer.close();
        }
    }
}
