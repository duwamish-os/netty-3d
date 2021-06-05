package com.netty.microservice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class NettyMicroServer {

    private static final boolean SSL = System.getProperty("ssl") != null;

    private ChannelFuture run() throws InterruptedException, CertificateException, SSLException {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .build();
        } else {
            sslCtx = null;
        }

        /**
         * with NettyRuntime.availableProcessors() * 2 = 24
         * Percentage of the requests served within a certain time (ms)
         *   50%      0
         *   66%      0
         *   75%      0
         *   80%      0
         *   90%      0
         *   95%      0
         *   98%      0
         *   99%      0
         *  100%     33 (longest request)
         */
        var cores = NettyRuntime.availableProcessors();
        int ioThreads1 = SystemPropertyUtil.getInt("io.netty.eventLoopThreads", cores * 2);


        //a multithreaded event loop that handles I/O operation
        var bossGroupEventLoop = new NioEventLoopGroup(ioThreads1);

        //accepts an incoming connection
        var workerGroupForHttpConnections = new NioEventLoopGroup();

        // NioServerSocketChannel used to instantiate a new Channel
        // to accept incoming connections
        var server = new ServerBootstrap();

        server.group(bossGroupEventLoop, workerGroupForHttpConnections)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpServerChannel(sslCtx));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        var serverTask = server.bind(9090).sync();

        return serverTask.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException, CertificateException, SSLException {
        new NettyMicroServer().run();
    }
}
