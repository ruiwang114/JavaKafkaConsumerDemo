package io.netty.example.http.websocketx.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.http.websocketx.base.Global;
import io.netty.example.http.websocketx.initializer.DownloadWebSocketServerInitializerSSL;
import io.netty.example.http.websocketx.initializer.HttpServerInitializer;
import io.netty.example.http.websocketx.util.DruidUtil;
import io.netty.example.http.websocketx.util.RedisUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * 下拉工程主类,包含工程启动主函数.
 *
 *
 * @author oldRi
 * Date 20191226
 */
@Slf4j
public final class DownloadWebSocketServerSSL {

    //加载入口参数
//    static final boolean SSL = System.getProperty("ssl") != null;
//    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

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
        //启动redis连接池，并初始化redis，druid连接池
        RedisUtil initRedis=new RedisUtil();
        DruidUtil initJdbc=new DruidUtil();
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
                    .childHandler(new DownloadWebSocketServerInitializerSSL());
//                    .childHandler(new HttpServerInitializer());

            Channel ch = b.bind(wssServicePort).sync().channel();

            log.info("wss服务已在本地端口： " + wssServicePort + "绑定并启动");

            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
