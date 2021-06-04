package com.netty.microservice;

import com.netty.microservice.handlers.HeartbeatServerJsonHandler;
import com.netty.microservice.handlers.HelloServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;

public class HttpServerChannel extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpServerChannel(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpServerExpectContinueHandler());
        pipeline.addLast(new HelloServerHandler());
//        pipeline.addLast(new HeartbeatServerJsonHandler());
    }
}
