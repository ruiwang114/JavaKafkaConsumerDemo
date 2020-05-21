package io.netty.example.http.websocketx.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.http.websocketx.factory.OpenSecureChatSslContextFactory;
import io.netty.example.http.websocketx.handler.WebSocketClientHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslHandler;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import java.net.URI;

public class ServerTask implements Runnable {

//    static final String URL = System.getProperty("url", "wss://localhost:9994/upload");
    static final String URL = System.getProperty("url", "wss://k01.weishi110.cn:9994/upload");

    @SneakyThrows
    @Override
    public void run() {
        {
            System.out.println("当前线程 : "+Thread.currentThread().getName());
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
                String cChatPath =  "/Users/aRi/Desktop/testIJGit/k01datatransfer/opencertsused/client.p12";
                String cTrustPath = "/Users/aRi/Desktop/testIJGit/k01datatransfer/opencertsused/root.p12";
                final SSLEngine engine = OpenSecureChatSslContextFactory.getClientContext(cChatPath,cTrustPath).createSSLEngine("k01.weishi110.cn",port);
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
                                        new HttpObjectAggregator(Integer.MAX_VALUE),
                                        WebSocketClientCompressionHandler.INSTANCE,
                                        handler);
                            }
                        });

                Channel ch = b.connect(uri.getHost(), port).sync().channel();
                handler.handshakeFuture().sync();
//                for (int i =0 ; i < 50000 ; i++){
                    WebSocketFrame frame = new TextWebSocketFrame("testtest");
                    ch.writeAndFlush(frame);
//                }
                while (true){
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }
    }
}
