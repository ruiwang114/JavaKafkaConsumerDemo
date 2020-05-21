/*
 * Copyright 2014 The Netty Project
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
package io.netty.example.http.websocketx.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.http.websocketx.factory.OpenSecureChatSslContextFactory;
import io.netty.example.http.websocketx.handler.WebSocketClientHandler;
import io.netty.example.http.websocketx.factory.SecureChatSslContextFactory;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;


public final class WebSocketClientSSL {

//    static final String URL = System.getProperty("url", "wss://127.0.0.1:3101/upload");
//    static final String URL = System.getProperty("url", "wss://k01.weishi110.cn:9995/download");
    static final String URL = System.getProperty("url", "wss://k01.weishi110.cn:9995/upload");

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
        final String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        final int port;
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            System.err.println("Only WS(S) is supported.");
            return;
        }


        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            final WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(),Integer.MAX_VALUE));

            Bootstrap b = new Bootstrap();
//            String cChatPath =  System.getProperty("user.dir")+"/src/main/java/io/netty/example/http/websocketx/conf/twoway/cChat.jks";
//            String cChatPath =  "/Users/aRi/Desktop/testIJGit/k01datatransfer/opencertsused/client.p12";
//            String cTrustPath = "/Users/aRi/Desktop/testIJGit/k01datatransfer/opencertsused/root.p12";

            String cChatPath =  "/Users/aRi/Desktop/testIJGit/k01datatransfer/cert0401/client.p12";
            String cTrustPath = "/Users/aRi/Desktop/testIJGit/k01datatransfer/cert0401/ca.p12";
            final SSLEngine engine = OpenSecureChatSslContextFactory.getClientContext(cChatPath,cTrustPath).createSSLEngine(host,port);
            engine.setUseClientMode(true);

            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline p = ch.pipeline();
//                     if (sslCtx != null) {
//                         p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
//                     }
                     p.addLast("ssl", new SslHandler(engine));
                     p.addLast(
                             new HttpClientCodec(),
                             new HttpObjectAggregator(8192),
                             WebSocketClientCompressionHandler.INSTANCE,
                             handler);
                 }
             });

            Channel ch = b.connect(uri.getHost(), port).sync().channel();
            handler.handshakeFuture().sync();

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            int i=0;

            while (true) {
                 System.out.println("发送一条消息.");
//                String msg = console.readLine();
                JSONObject json1=new JSONObject();
                JSONObject json2=new JSONObject();
                JSONArray array=new JSONArray();
                json1.put("company","5");
                json1.put("total",2);
//                json2.put("r_info_type",1);
                array.add(JSONObject.parse("{\n" +
                        "\t\t\"r_info_type\": 5,\n" +
                        "\t\t\"r_time_i\": 1581137278,\n" +
                        "\t\t\"r_time_s\": \"2020-02-08 12:47:58\",\n" +
                        "\t\t\"r_sip_s\": \"192.168.30.1\",\n" +
                        "\t\t\"r_dip_s\": \"192.168.30.55\",\n" +
                        "\t\t\"r_mode\": 3,\n" +
                        "\t\t\"r_info_id\": 14,\n" +
                        "\t\t\"r_group_id\": 207,\n" +
                        "\t\t\"r_rule_id\": 60377,\n" +
                        "\t\t\"r_policy_hit\": 1,\n" +
                        "\t\t\"r_sport\": 0,\n" +
                        "\t\t\"r_dport\": 80,\n" +
                        "\t\t\"r_action\": 1,\n" +
                        "\t\t\"r_protocol\": 6,\n" +
                        "\t\t\"r_device\": \"K01\",\n" +
                        "\t\t\"r_severity\": 2,\n" +
                        "\t\t\"r_country\": \"中国\",\n" +
                        "\t\t\"r_province\": \"广东\",\n" +
                        "\t\t\"r_city\": \"\",\n" +
                        "\t\t\"r_xffip\": \"\",\n" +
                        "\t\t\"r_serverid\": \"31D0B524FDCEEFAF4FF90486D3A8D0F0\",\n" +
                        "\t\t\"r_buff\": \"f, image/x-xbitmap, image/jpeg, image/pjpeg, */*\\r\\nUser-Agent: XXX\\r\\nHost: 192.168.30.55\\r\\nConnection: Keep-Alive \",\n" +
                        "\t\t\"r_math_sign\": \"User-Agent: XXX\\r\\nHost: 192.168.30.55\\r\\nConnection: Keep-Alive\\r \",\n" +
                        "\t\t\"r_url\": \"/home.php\",\n" +
                        "\t\t\"r_hostname\": \"192.168.30.55\"\n" +
                        "\t}"));
                array.add(JSONObject.parse("{\n" +
                        "\t\t\"r_info_type\": 5,\n" +
                        "\t\t\"r_time_i\": 1581137278,\n" +
                        "\t\t\"r_time_s\": \"2020-02-08 12:47:58\",\n" +
                        "\t\t\"r_sip_s\": \"192.168.30.1\",\n" +
                        "\t\t\"r_dip_s\": \"192.168.30.55\",\n" +
                        "\t\t\"r_mode\": 3,\n" +
                        "\t\t\"r_info_id\": 14,\n" +
                        "\t\t\"r_group_id\": 207,\n" +
                        "\t\t\"r_rule_id\": 60377,\n" +
                        "\t\t\"r_policy_hit\": 1,\n" +
                        "\t\t\"r_sport\": 0,\n" +
                        "\t\t\"r_dport\": 80,\n" +
                        "\t\t\"r_action\": 1,\n" +
                        "\t\t\"r_protocol\": 6,\n" +
                        "\t\t\"r_device\": \"K01\",\n" +
                        "\t\t\"r_severity\": 2,\n" +
                        "\t\t\"r_country\": \"中国\",\n" +
                        "\t\t\"r_province\": \"广东\",\n" +
                        "\t\t\"r_city\": \"\",\n" +
                        "\t\t\"r_xffip\": \"\",\n" +
                        "\t\t\"r_serverid\": \"31D0B524FDCEEFAF4FF90486D3A8D0F0\",\n" +
                        "\t\t\"r_buff\": \"f, image/x-xbitmap, image/jpeg, image/pjpeg, */*\\r\\nUser-Agent: XXX\\r\\nHost: 192.168.30.55\\r\\nConnection: Keep-Alive \",\n" +
                        "\t\t\"r_math_sign\": \"User-Agent: XXX\\r\\nHost: 192.168.30.55\\r\\nConnection: Keep-Alive\\r \",\n" +
                        "\t\t\"r_url\": \"/home.php\",\n" +
                        "\t\t\"r_hostname\": \"192.168.30.55\"\n" +
                        "\t}"));
                json1.put("data",array);


                if (json1 == null) {
                    break;
//                } else if ("bye".equals(msg.toLowerCase())) {
//                    ch.writeAndFlush(new CloseWebSocketFrame());
//                    ch.closeFuture().sync();
//                    break;
//                } else if ("ping".equals(msg.toLowerCase())) {
//                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
//                    ch.writeAndFlush(frame);
                } else {
                    WebSocketFrame frame = new TextWebSocketFrame(json1.toString());
                    ch.writeAndFlush(frame);
                }
                System.out.println("休息1.5秒");
                Thread.sleep(1500);
            }

//            while (true) {
//                System.out.println("第  "+i+"  次输入信息：");
////                String msg = console.readLine();
//                String msg="{\"offset\":1,\"serial_num\":\"authcodeHou\"}";
//                if (msg == null) {
//                    break;
//                } else if ("bye".equals(msg.toLowerCase())) {
//                    ch.writeAndFlush(new CloseWebSocketFrame());
//                    ch.closeFuture().sync();
//                    break;
//                } else if ("ping".equals(msg.toLowerCase())) {
//                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
//                    ch.writeAndFlush(frame);
//                } else {
//                    WebSocketFrame frame = new TextWebSocketFrame(msg);
//                    System.out.println( System.currentTimeMillis());
//                    ch.writeAndFlush(frame);
//                }
//                i++;
//                if(i==100) {
//                    System.out.println("休息60000秒");
//                    Thread.sleep(60000000);
//                }
//            }
        } finally {
            group.shutdownGracefully();
        }
    }

}
